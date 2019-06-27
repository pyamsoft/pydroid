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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class BaseUiView<S : UiViewState, V : UiViewEvent> protected constructor(
  parent: ViewGroup
) : UiView<S, V>() {

  protected abstract val layoutRoot: View

  protected abstract val layout: Int

  private var _parent: ViewGroup? = parent
  private var boundViews: MutableSet<BoundView<*>>? = null

  private fun die(): Nothing {
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
    return layoutRoot.id
  }

  final override fun doInflate(savedInstanceState: Bundle?) {
    assertValidState()

    parent().inflateAndAdd(layout) {
      onInflated(this, savedInstanceState)
    }
  }

  protected open fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {
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
    onSaveState(outState)
  }

  protected open fun onSaveState(outState: Bundle) {
  }

  final override fun doTeardown() {
    assertValidState()
    onTeardown()

    parent().removeView(layoutRoot)
    boundViews?.forEach { it.teardown() }
    boundViews?.clear()

    boundViews = null
    _parent = null
  }

  protected open fun onTeardown() {
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

