/*
 * Copyright 2022 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui.theme

import android.app.Activity
import android.content.res.Configuration
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Handles getting current dark mode state and setting dark mode state */
internal class ThemingImpl
internal constructor(
    private val preferences: ThemingPreferences,
) : Theming {

  override suspend fun init() =
      withContext(context = Dispatchers.IO) {
        // Make sure we set the AppCompatDelegate from the saved preference mode
        val mode = getMode()
        applyDarkTheme(mode)
      }

  /** Is activity dark mode */
  override fun isDarkTheme(activity: Activity): Boolean {
    val uiMode = activity.resources.configuration.uiMode
    return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
  }

  /** Which mode are we in right now? */
  override suspend fun getMode(): Theming.Mode {
    return preferences.getDarkMode()
  }

  /** Set application wide dark mode */
  override suspend fun setDarkTheme(mode: Theming.Mode) =
      withContext(context = Dispatchers.IO) {
        preferences.setDarkMode(mode)
        applyDarkTheme(mode)
      }

  private suspend fun applyDarkTheme(mode: Theming.Mode) =
      withContext(context = Dispatchers.Main) {
        // Needs to run on main thread
        val appCompatMode = mode.toAppCompatMode()
        AppCompatDelegate.setDefaultNightMode(appCompatMode)
      }

  @CheckResult
  private fun Theming.Mode.toAppCompatMode(): Int =
      when (this) {
        Theming.Mode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        Theming.Mode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        Theming.Mode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
      }
}
