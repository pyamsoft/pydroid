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
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import kotlin.LazyThreadSafetyMode.NONE

abstract class PrefUiView<T : Any> protected constructor(
  protected val parent: PreferenceScreen,
  protected val callback: T
) : UiView {

  private val lazyPrefInitializer = { parent }

  final override fun id(): Int {
    throw InvalidIdException
  }

  override fun inflate(savedInstanceState: Bundle?) {
  }

  override fun saveState(outState: Bundle) {
  }

  override fun teardown() {
  }

  @CheckResult
  protected fun <T : Preference> lazyPref(key: String): Lazy<T> {
    return lazy(NONE) {
      @Suppress("UNCHECKED_CAST")
      return@lazy parent.findPreference(key) as T
    }
  }

  @CheckResult
  protected fun <T : Preference> lazyPref(@StringRes id: Int): Lazy<T> {
    return lazy(NONE) {
      @Suppress("UNCHECKED_CAST")
      return@lazy parent.findPreference(parent.context.getString(id)) as T
    }
  }

}
