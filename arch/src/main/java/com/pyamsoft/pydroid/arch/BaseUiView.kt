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

abstract class BaseUiView<C : Any> protected constructor(
  parent: ViewGroup,
  callback: C
) : UiView {

  protected abstract val layoutRoot: View

  protected abstract val layout: Int

  private var _parent: ViewGroup? = parent

  private var _callback: C? = callback
  protected val callback: C
    get() = _callback ?: die()

  private var boundViews: MutableSet<BoundView<*>>? = null

  final override fun id(): Int {
    return layoutRoot.id
  }

  private fun die(): Nothing {
    throw IllegalStateException("Cannot call UiView methods after it has been torn down")
  }

  @CheckResult
  private fun parent(): ViewGroup {
    return _parent ?: die()
  }

  private fun assertValidState() {
    if (_parent == null || _callback == null) {
      die()
    }
  }

  final override fun inflate(savedInstanceState: Bundle?) {
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

  final override fun saveState(outState: Bundle) {
    assertValidState()

    onSaveState(outState)
  }

  protected open fun onSaveState(outState: Bundle) {

  }

  final override fun teardown() {
    assertValidState()

    onTeardown()

    parent().removeView(layoutRoot)

    boundViews?.forEach { it.teardown() }
    boundViews?.clear()

    boundViews = null
    _parent = null
    _callback = null
  }

  protected open fun onTeardown() {

  }

  private inline fun ViewGroup.inflateAndAdd(@LayoutRes layout: Int, findViews: View.() -> Unit) {
    LayoutInflater.from(context)
        .inflate(layout, this, true)
        .run(findViews)
  }

  @Deprecated(
      message = "Use boundView",
      replaceWith = ReplaceWith(
          expression = "boundView<T>(id)"
      )
  )
  @CheckResult
  protected fun <T : View> lazyView(@IdRes id: Int): Lazy<T> {
    assertValidState()

    return lazy(NONE) { parent().findViewById<T>(id) }
  }

  @CheckResult
  protected fun <T : View> boundView(@IdRes id: Int): BoundView<T> {
    assertValidState()

    return BoundView<T>(parent(), id).also { v ->
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

  protected class BoundView<T : View> internal constructor(
    parent: ViewGroup,
    @IdRes private val id: Int
  ) : ReadOnlyProperty<Any, T> {

    private var parent: ViewGroup? = parent
    private var view: T? = null

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
    ): T {
      assertValidState()

      val v: T? = view
      val result: T
      if (v == null) {
        val bound = requireNotNull(parent().findViewById<T>(id))
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

