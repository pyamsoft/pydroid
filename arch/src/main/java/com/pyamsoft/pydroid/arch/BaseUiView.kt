/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.arch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.annotation.UiThread
import androidx.viewbinding.ViewBinding
import com.pyamsoft.pydroid.core.Enforcer
import timber.log.Timber
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class BaseUiView<S : UiViewState, V : UiViewEvent, B : ViewBinding> protected constructor(
    parent: ViewGroup
) : UiView<S, V>() {

    protected abstract val layoutRoot: View

    private val nestedViewDelegate = lazy(NONE) { mutableListOf<UiView<S, V>>() }
    private val nestedViews by nestedViewDelegate

    private var bound: MutableSet<Bound<*>>? = null

    private var _parent: ViewGroup? = parent

    protected abstract val viewBinding: (LayoutInflater, ViewGroup) -> B

    private var _binding: B? = null
    protected val binding: B
        get() = _binding ?: die("Null binding")

    init {
        doOnInflate {
            assertValidState()

            // We place a check for the id here because at the point that the binding is used
            // the layoutRoot must not be null and must be resolved so that the teardown works
            // correctly - otherwise you will get a state error.
            require(id() != 0) { "id() must not equal 0! " }
        }

        doOnTeardown {
            assertValidState()

            // Teardown must happen in this order
            // This is because the layoutRoot may actually just be a bound view itself
            // So we cannot call teardown to null out the views until it is also torn down
            // just incase.
            bound?.forEach { it.remove() }
            layoutRoot.teardown(parent)
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
        }
    }

    final override fun onFinalTeardown() {
        Timber.d("Teardown complete, unbind")
        _binding = null
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
        nestedViews().forEach { nestedView ->
            prepareNestedViewInit(nestedView)
            nestedView.init(savedInstanceState)
        }
    }

    private fun prepareNestedViewInit(view: UiView<S, V>) {
        if (view !is BaseUiView<S, V, *>) {
            return
        }

        val root = layoutRoot
        if (root is ViewGroup) {
            view.adopt(root)
        } else {
            throw IllegalStateException("Cannot initialize nested view. This BaseUiView layoutRoot must be a ViewGroup.")
        }
    }

    /**
     * Adopt the view to this UiView parent
     * Otherwise the child view will inflate into the same parent instead of the parent layoutRoot
     */
    private fun adopt(parent: ViewGroup) {
        _parent = parent

        // Make sure after adoption we are still valid
        assertValidState()
    }

    @CheckResult
    internal fun nestedViews(): List<UiView<S, V>> {
        return if (nestedViewDelegate.isInitialized()) nestedViews else emptyList()
    }

    private fun die(reason: String): Nothing {
        throw IllegalStateException("Kill BaseUiView: $reason")
    }

    @CheckResult
    private fun parent(): ViewGroup {
        return _parent ?: die("Null parent")
    }

    private fun assertValidState() {
        if (_parent == null) {
            die("Null parent")
        }
    }

    @IdRes
    @CheckResult
    fun id(): Int {
        val id = layoutRoot.id
        require(id != 0) { "id() must not equal 0! " }
        return id
    }

    final override fun render(state: S) {
        assertValidState()
        onRender(state)
        nestedViews().forEach { it.render(state) }
    }

    @UiThread
    protected abstract fun onRender(state: S)

    private fun inflateAndAddToParent(
        inflater: LayoutInflater,
        parent: ViewGroup
    ) {
        _binding = viewBinding.invoke(inflater, parent)
    }

    /**
     * Convenience hook for nested UiView<S, V> instances
     *
     * Nesting a UiView will make its parent element become this UiView's layoutRoot.
     * This will allow you to embed UiViews inside of other UiView objects.
     *
     * NOTE: Not thread safe, Main thread only for now.
     */
    @UiThread
    protected fun nest(vararg views: UiView<S, V>) {
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
    @UiThread
    protected fun <V : View> boundView(func: B.() -> V): Bound<V> {
        Enforcer.assertOnMainThread()

        assertValidState()
        return Bound(parent()) { func(binding) }.also { trackBound(it) }
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
            bound?.teardown(requireNotNull(parent))
        }

        internal fun teardown() {
            assertValidState()

            bound = null
            parent = null
            resolver = null
        }
    }

    companion object {

        private fun View.teardown(parent: ViewGroup) {
            // Clear all messages on the view handler before removing it from the view group
            parent.removeView(this)
        }
    }
}
