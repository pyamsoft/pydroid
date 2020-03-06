/*
 * Copyright 2019 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.pydroid.arch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class BaseUiView<S : UiViewState, V : UiViewEvent> protected constructor(
    parent: ViewGroup
) : UiView<S, V>() {

    protected abstract val layoutRoot: View

    protected abstract val layout: Int

    private val nestedViewDelegate = lazy(NONE) { mutableListOf<IView<S, V>>() }
    private val nestedViews by nestedViewDelegate

    private val nestedInitDelegate = lazy(NONE) { mutableSetOf<(IView<S, V>) -> Unit>() }
    private val nestedInits by nestedInitDelegate

    private var bound: MutableSet<Bound<*>>? = null

    private var _parent: ViewGroup? = parent

    init {
        doOnInflate {
            assertValidState()
        }

        doOnNestedInit { view ->
            if (view !is BaseUiView<S, V>) {
                return@doOnNestedInit
            }

            val root = layoutRoot
            if (root is ViewGroup) {
                // Adopt the view to this UiView parent
                // Otherwise the child view will inflate into the same parent instead of the parent layoutRoot
                view._parent = root
            } else {
                throw IllegalStateException("Cannot initialize nested view. This UiView layoutRoot must be a ViewGroup.")
            }
        }

        doOnTeardown {
            assertValidState()

            // Teardown must happen in this order
            bound?.forEach { it.remove() }
            parent.removeView(layoutRoot)
            bound?.forEach { it.teardown() }
            bound?.clear()
            bound = null

            _parent = null
        }

        doOnTeardown {
            // If there are any nested views hanging around, clear them out too
            if (nestedViewDelegate.isInitialized()) {
                nestedViews.clear()
            }

            // If there are any nested init hooks hanging around, clear them out too
            if (nestedInitDelegate.isInitialized()) {
                nestedInits.clear()
            }
        }
    }

    final override fun onInit(savedInstanceState: UiBundleReader) {
        assertValidState()

        val parent = parent()
        val inflater = LayoutInflater.from(parent.context)
        inflateAndAddToParent(inflater, parent)

        initNestedViews(savedInstanceState)
    }

    private fun initNestedViews(savedInstanceState: UiBundleReader) {
        // Only run the initialization hooks if they exist, otherwise we don't need to init the memory
        if (nestedViewDelegate.isInitialized()) {

            // Call init hooks in FIFO order
            for (nestedView in nestedViews) {
                if (nestedInitDelegate.isInitialized()) {
                    for (nestedInit in nestedInits) {
                        nestedInit(nestedView)
                    }
                }
                nestedView.init(savedInstanceState)
            }

            // Don't clear the nestedViews list yet, we still need it
            if (nestedInitDelegate.isInitialized()) {
                nestedInits.clear()
            }
        }
    }

    @CheckResult
    internal fun nestedViews(): Array<out IView<S, V>> {
        return if (nestedViewDelegate.isInitialized()) nestedViews.toTypedArray() else emptyArray()
    }

    internal fun die(): Nothing {
        throw IllegalStateException("Cannot call UiView methods after it has been torn down")
    }

    @CheckResult
    private fun parent(): ViewGroup {
        return _parent ?: die()
    }

    private fun assertValidState() {
        if (_parent == null) {
            die()
        }
    }

    final override fun id(): Int {
        val id = layoutRoot.id
        assert(id != 0) { "id() must not equal 0! " }
        return id
    }

    final override fun render(state: S) {
        assertValidState()
        onRender(state)
    }

    protected abstract fun onRender(state: S)

    internal open fun inflateAndAddToParent(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) {
        inflater.inflate(layout, parent, true)
    }

    /**
     * Use this to run an event before a UiView initializes its nested UiView children
     * Events are not guaranteed to run in any consistent order
     *
     * This is generally used in something like the constructor
     *
     * init {
     *     doOnNestedInit { view ->
     *         ...
     *     }
     * }
     *
     */
    protected fun doOnNestedInit(onNestedInit: (view: IView<S, V>) -> Unit) {
        nestedInits.add(onNestedInit)
    }

    /**
     * Convenience hook for nested IView<S, V> instances
     */
    protected fun nest(vararg views: IView<S, V>) {
        views.forEach { view ->
            nestedViews.add(view)
            doOnInflate { view.inflate(it) }
            doOnSaveState { view.saveState(it) }
            doOnTeardown { view.teardown() }
        }
    }

    private fun trackBound(v: Bound<*>) {
        assertValidState()

        val bv: MutableSet<Bound<*>>? = bound
        val mutateMe: MutableSet<Bound<*>>
        if (bv == null) {
            val bound = LinkedHashSet<Bound<*>>()
            this.bound = bound
            mutateMe = bound
        } else {
            mutateMe = bv
        }

        mutateMe.add(v)
    }

    @CheckResult
    @Deprecated(message = "Use ViewBinding: BindingUiView<S,V,B>.binding or BindingUiView<S,V,B>.boundView() instead")
    // NOTE: This function will be removed in the next major version 21.X.X
    protected fun <V : View> boundView(@IdRes id: Int): Bound<V> {
        return createBound { parent ->
            // Need explicit type here or kotlinc complains
            parent.findViewById<V>(id)
        }
    }

    @CheckResult
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    // NOTE: This function will be removed in the next major version 21.X.X
    protected fun <V : View> createBound(resolver: (View) -> V): Bound<V> {
        assertValidState()
        return Bound(parent(), resolver).also { trackBound(it) }
    }

    protected data class Bound<V : View> internal constructor(
        private var parent: ViewGroup?,
        private var resolver: ((View) -> V)?
    ) : ReadOnlyProperty<Any, V> {

        private var bound: V? = null

        private fun die(): Nothing {
            throw IllegalStateException("Cannot call BoundView methods after it has been torn down")
        }

        private fun assertValidState() {
            if (resolver == null || parent == null) {
                die()
            }
        }

        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): V {
            assertValidState()

            val b: V? = bound
            val result: V
            if (b == null) {
                val r = requireNotNull(resolver)
                val bound = r(requireNotNull(parent))
                this.bound = bound
                result = bound
            } else {
                result = b
            }

            return result
        }

        internal fun remove() {
            assertValidState()
            bound.let { requireNotNull(parent).removeView(it) }
        }

        internal fun teardown() {
            assertValidState()

            bound = null
            parent = null
            resolver = null
        }
    }
}
