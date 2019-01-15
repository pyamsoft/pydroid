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

package com.pyamsoft.pydroid.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.CheckResult
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.ui.R
import timber.log.Timber

class Theming internal constructor(context: Context) {

  private val preferences: SharedPreferences
  private val key: String

  init {
    val appContext = context.applicationContext
    preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    key = appContext.getString(R.string.dark_mode_key)

    if (!preferences.contains(key)) {
      setDarkTheme(IS_DEFAULT_DARK_THEME)
    }
  }

  @CheckResult
  fun isDarkTheme(): Boolean {
    return preferences.getBoolean(key, IS_DEFAULT_DARK_THEME)
  }

  @JvmOverloads
  fun setDarkTheme(
    dark: Boolean,
    onSet: (dark: Boolean) -> Unit = {}
  ) {
    preferences.edit {
      Timber.d("Set dark theme: $dark")
      putBoolean(key, dark)
      onSet(dark)
    }
  }

  companion object {

    @JvmField
    var IS_DEFAULT_DARK_THEME = false
  }
}
