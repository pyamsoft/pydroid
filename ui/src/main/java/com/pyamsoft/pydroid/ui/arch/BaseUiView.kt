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

package com.pyamsoft.pydroid.ui.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.pyamsoft.pydroid.core.bus.Publisher
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class BaseUiView<T : ViewEvent> protected constructor(
  private val parent: ViewGroup,
  private val bus: Publisher<T>
) : UiView {

  private val lazyViewInitializer = { parent }

  protected abstract val layout: Int

  final override fun inflate(savedInstanceState: Bundle?) {
    parent.inflateAndAdd(layout) {
      onInflated(this, savedInstanceState)
    }
  }

  protected open fun onInflated(
    view: View,
    savedInstanceState: Bundle?
  ) {

  }

  override fun saveState(outState: Bundle) {
  }

  override fun teardown() {
  }

  protected fun publish(event: T) {
    bus.publish(event)
  }

  private inline fun ViewGroup.inflateAndAdd(@LayoutRes layout: Int, findViews: View.() -> Unit = {}) {
    LayoutInflater.from(context)
        .inflate(layout, this, true)
        .run(findViews)
  }

  @JvmOverloads
  @CheckResult
  protected fun <T : View> lazyView(
    @IdRes id: Int,
    initializer: () -> View = lazyViewInitializer
  ): LazyFindView<T> {
    return LazyFindView(id, initializer)
  }

  protected class LazyFindView<T : View> internal constructor(
    @IdRes private val id: Int,
    private val initializer: () -> View
  ) : ReadOnlyProperty<Any?, T> {

    private var foundView: T? = null

    override fun getValue(
      thisRef: Any?,
      property: KProperty<*>
    ): T {
      if (foundView == null) {
        val rootView = initializer()
        foundView = rootView.findViewById(id)
      }

      return requireNotNull(foundView)
    }

  }

}

