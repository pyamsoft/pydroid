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

import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelProviders
import com.pyamsoft.pydroid.arch.UiViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiViewModel<*, *, *>> Fragment.factory(
  crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
  return ViewModelFactory(this, T::class.java) { requireNotNull(factoryProvider()) }
}

/**
 * Allow nullable for easier caller API
 */
@CheckResult
inline fun <reified T : UiViewModel<*, *, *>> FragmentActivity.factory(
  crossinline factoryProvider: () -> Factory?
): ViewModelFactory<T> {
  return ViewModelFactory(this, T::class.java) { requireNotNull(factoryProvider()) }
}

class ViewModelFactory<T : UiViewModel<*, *, *>> private constructor(
  private val fragment: Fragment?,
  private val activity: FragmentActivity?,
  private val type: Class<T>,
  private val factoryProvider: () -> Factory
) : ReadOnlyProperty<Any, T> {

  constructor(
    fragment: Fragment,
    type: Class<T>,
    factoryProvider: () -> Factory
  ) : this(fragment, null, type, factoryProvider)

  constructor(
    activity: FragmentActivity,
    type: Class<T>,
    factoryProvider: () -> Factory
  ) : this(null, activity, type, factoryProvider)

  private val lock = Any()
  @Volatile private var value: T? = null

  private fun attachToLifecycle(lifecycle: Lifecycle) {
    lifecycle.addObserver(object : LifecycleObserver {

      @Suppress("unused")
      @OnLifecycleEvent(ON_DESTROY)
      fun onDestroy() {
        lifecycle.removeObserver(this)
        value = null
      }

    })
  }

  @CheckResult
  private fun resolveValue(): T {
    fragment?.let { f ->
      attachToLifecycle(f.viewLifecycleOwner.lifecycle)
      return ViewModelProviders.of(f, factoryProvider())
          .get(type)
    }
    activity?.let { a ->
      attachToLifecycle(a.lifecycle)
      return ViewModelProviders.of(a, factoryProvider())
          .get(type)
    }
    throw IllegalStateException("Both Fragment an Activity are null")
  }

  override fun getValue(
    thisRef: Any,
    property: KProperty<*>
  ): T {
    val v = value
    if (v != null) {
      return v
    }

    if (value == null) {
      synchronized(lock) {
        if (value == null) {
          value = resolveValue()
        }
      }
    }

    return requireNotNull(value)
  }
}
