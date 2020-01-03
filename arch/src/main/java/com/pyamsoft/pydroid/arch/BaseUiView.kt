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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
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

    private var _parent: ViewGroup? = parent
    private var boundViews: MutableSet<BoundView<*>>? = null

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
            parent().removeView(layoutRoot)
            boundViews?.forEach { it.teardown() }
            boundViews?.clear()
            boundViews = null
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

    final override fun onInit(savedInstanceState: Bundle?) {
        assertValidState()
        parent().inflateAndAdd(layout)

        // Only run the initialization hooks if they exist, otherwise we don't need to init the memory
        if (nestedViewDelegate.isInitialized()) {

            // Call init hooks in FIFO order
            for (nestedView in nestedViews) {
                onWillInitNested(nestedView)
                nestedView.init(savedInstanceState)
            }

            // Don't clear the nestedViews list yet, we still need it
            if (nestedInitDelegate.isInitialized()) {
                nestedInits.clear()
            }
        }
    }

    private fun onWillInitNested(view: IView<S, V>) {
        if (nestedInitDelegate.isInitialized()) {
            for (nestedInit in nestedInits) {
                nestedInit(view)
            }
        }
    }

    @CheckResult
    internal fun nestedViews(): Array<out IView<S, V>> {
        return if (nestedViewDelegate.isInitialized()) nestedViews.toTypedArray() else emptyArray()
    }

    private fun die(): Nothing {
        throw IllegalStateException("Cannot call UiView methods after it has been torn down")
    }

    @CheckResult
    protected fun parent(): ViewGroup {
        return _parent ?: die()
    }

    private fun assertValidState() {
        if (_parent == null) {
            die()
        }
    }

    final override fun id(): Int {
        return layoutRoot.id
    }

    final override fun render(
        state: S,
        savedState: UiSavedState
    ) {
        assertValidState()
        onRender(state, savedState)
    }

    protected abstract fun onRender(
        state: S,
        savedState: UiSavedState
    )

    private fun ViewGroup.inflateAndAdd(@LayoutRes layout: Int) {
        LayoutInflater.from(context)
            .inflate(layout, this, true)
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

    @CheckResult
    protected fun <V : View> boundView(@IdRes id: Int): BoundView<V> {
        assertValidState()

        return BoundView<V>(parent(), id)
            .also { v ->
                assertValidState()

                val bv: MutableSet<BoundView<*>>? = boundViews
                val mutateMe: MutableSet<BoundView<*>>
                if (bv == null) {
                    val bound = LinkedHashSet<BoundView<*>>()
                    boundViews = bound
                    mutateMe = bound
                } else {
                    mutateMe = bv
                }

                mutateMe.add(v)
            }
    }

    protected class BoundView<V : View> internal constructor(
        parent: ViewGroup,
        @IdRes private val id: Int
    ) : ReadOnlyProperty<Any, V> {

        private var parent: ViewGroup? = parent
        private var view: V? = null

        private fun die(): Nothing {
            throw IllegalStateException("Cannot call BoundView methods after it has been torn down")
        }

        @CheckResult
        private fun parent(): ViewGroup {
            return parent ?: die()
        }

        private fun assertValidState() {
            if (parent == null) {
                die()
            }
        }

        override fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ): V {
            assertValidState()

            val v: V? = view
            val result: V
            if (v == null) {
                val bound = requireNotNull(parent().findViewById<V>(id))
                view = bound
                result = bound
            } else {
                result = v
            }

            return result
        }

        internal fun teardown() {
            assertValidState()

            parent = null
            view = null
        }
    }
}
