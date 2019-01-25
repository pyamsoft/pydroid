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
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.core.bus.Publisher
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class PrefUiView<T : ViewEvent> protected constructor(
  protected val parent: PreferenceScreen,
  private val bus: Publisher<T>
) : UiView {

  private val lazyPrefInitializer = { parent }

  protected fun publish(event: T) {
    bus.publish(event)
  }

  @JvmOverloads
  @CheckResult
  protected fun <T : Preference> lazyPref(
    @StringRes id: Int,
    initializer: () -> PreferenceScreen = lazyPrefInitializer
  ): LazyFindPref<T> {
    return LazyFindPref(id, "", initializer)
  }

  @JvmOverloads
  @CheckResult
  protected fun <T : Preference> lazyPref(
    key: String,
    initializer: () -> PreferenceScreen = lazyPrefInitializer
  ): LazyFindPref<T> {
    return LazyFindPref(0, key, initializer)
  }

  protected class LazyFindPref<T : Preference> internal constructor(
    @StringRes private val id: Int,
    private val key: String,
    private val initializer: () -> PreferenceScreen
  ) : ReadOnlyProperty<Any?, T> {

    private var foundView: T? = null

    override fun getValue(
      thisRef: Any?,
      property: KProperty<*>
    ): T {
      if (foundView == null) {
        val rootPref = initializer()
        when {
          id == 0 -> {
            @Suppress("UNCHECKED_CAST")
            foundView = rootPref.findPreference(rootPref.context.getString(id)) as T
          }
          key.isNotBlank() -> {
            @Suppress("UNCHECKED_CAST")
            foundView = rootPref.findPreference(key) as T
          }
          else -> throw IllegalStateException("LazyFindPref must have either an id or a key")
        }
      }

      return requireNotNull(foundView)
    }

  }

}

