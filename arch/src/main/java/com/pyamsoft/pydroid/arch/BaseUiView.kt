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

abstract class BaseUiView<C : Any> protected constructor(
  parent: ViewGroup,
  callback: C
) : UiView {

  protected abstract val layoutRoot: View

  protected abstract val layout: Int

  private var _parent: ViewGroup? = parent

  private var _callback: C? = callback
  protected val callback: C
    get() = requireNotNull(_callback)

  final override fun id(): Int {
    return layoutRoot.id
  }

  @CheckResult
  private fun parent(): ViewGroup {
    return requireNotNull(_parent)
  }

  final override fun inflate(savedInstanceState: Bundle?) {
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
    onSaveState(outState)
  }

  protected open fun onSaveState(outState: Bundle) {

  }

  final override fun teardown() {
    onTeardown()

    parent().removeView(layoutRoot)
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

  @CheckResult
  protected fun <T : View> lazyView(@IdRes id: Int): Lazy<T> {
    return lazy(NONE) { parent().findViewById<T>(id) }
  }
}

