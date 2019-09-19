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
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class BaseUiView<S : UiViewState, V : UiViewEvent> protected constructor(
    parent: ViewGroup
) : UiView<S, V>() {

    protected abstract val layoutRoot: View

    protected abstract val layout: Int

    private var _parent: ViewGroup? = parent
    private var boundViews: MutableSet<BoundView<*>>? = null

    init {
        doOnInflate { savedInstanceState ->
            assertValidState()

            parent().inflateAndAdd(layout) {
                // NOTE: The deprecated function call is kept around for compat purposes.
                onInflated(this, savedInstanceState)
            }
        }
        doOnTeardown {
            assertValidState()

            // NOTE: The deprecated function call is kept around for compat purposes.
            onTeardown()

            parent().removeView(layoutRoot)
            boundViews?.forEach { it.teardown() }
            boundViews?.clear()

            boundViews = null
            _parent = null
        }
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

    final override fun doInflate(savedInstanceState: Bundle?) {
        // NOTE: The deprecated function call is kept around for compat purposes.
        // Intentionally blank
    }

    @Deprecated("Use doOnInflate { savedInstanceState: Bundle? -> } instead.")
    protected open fun onInflated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // NOTE: The deprecated function call is kept around for compat purposes.
        // Intentionally blank
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

    final override fun saveState(outState: Bundle) {
        assertValidState()

        // NOTE: The deprecated function call is kept around for compat purposes.
        onSaveState(outState)

        // Must call super - this is currently "deprecated" but only to encourage external consumers
        // to move away from the saveState method directly.
        //
        // It will continue to be used internally in the library and will be closed
        // and un-deprecated in the future.
        super.saveState(outState)
    }

    @Deprecated("Use doOnSaveState { outState: Bundle -> } instead.")
    protected open fun onSaveState(outState: Bundle) {
        // NOTE: The deprecated function call is kept around for compat purposes.
        // Intentionally blank
    }

    final override fun doTeardown() {
        // NOTE: The deprecated function call is kept around for compat purposes.
        // Intentionally blank
    }

    @Deprecated("Use doOnTeardown { () -> } instead.")
    protected open fun onTeardown() {
        // NOTE: The deprecated function call is kept around for compat purposes.
        // Intentionally blank
    }

    private inline fun ViewGroup.inflateAndAdd(@LayoutRes layout: Int, findViews: View.() -> Unit) {
        LayoutInflater.from(context)
            .inflate(layout, this, true)
            .run(findViews)
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
