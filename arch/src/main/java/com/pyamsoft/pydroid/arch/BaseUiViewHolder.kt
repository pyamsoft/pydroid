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
import androidx.annotation.LayoutRes

abstract class BaseUiViewHolder<S : UiViewState, V : UiViewEvent> protected constructor(
    parent: ViewGroup
) : BindableUiView<S, V>() {

    protected abstract val layoutRoot: View

    protected abstract val layout: Int

    private var _parent: ViewGroup? = parent
    private var boundViews: MutableSet<BoundView<*>>? = null

    init {
        doOnInflate {
            assertValidState()
            parent().inflateAndAdd(layout)
        }

        doOnTeardown {
            assertValidState()

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

    final override fun bind(state: S) {
        assertValidState()
        onBind(state)
    }

    final override fun unbind() {
        if (_parent != null) {
            onUnbind()
        }
    }

    protected abstract fun onBind(state: S)

    protected abstract fun onUnbind()

    private fun ViewGroup.inflateAndAdd(@LayoutRes layout: Int) {
        LayoutInflater.from(context)
            .inflate(layout, this, true)
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
}
