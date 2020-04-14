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

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class Theming internal constructor(preferences: ThemingPreferences) {

    init {
        // NOTE: We use GlobalScope here because this is an application level thing
        // Maybe its an anti-pattern but I think in controlled use, its okay.
        GlobalScope.launch {
            preferences.initializeDarkMode { mode ->
                setDarkTheme(mode) { newMode ->
                    Timber.d("Theming initialized mode: $newMode")
                }
            }

            val mode = preferences.getDarkMode()
            setDarkTheme(mode)
        }
    }

    @CheckResult
    fun isDarkTheme(activity: Activity): Boolean {
        val uiMode = activity.resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    @JvmOverloads
    fun setDarkTheme(
        mode: Mode,
        onSet: (mode: Mode) -> Unit = DEFAULT_ON_SET_CALLBACK
    ) {
        AppCompatDelegate.setDefaultNightMode(mode.toAppCompatMode())
        onSet(mode)
    }

    enum class Mode {
        LIGHT,
        DARK,
        SYSTEM;

        @CheckResult
        fun toRawString(): String {
            return name.toLowerCase(Locale.getDefault())
        }

        @CheckResult
        fun toAppCompatMode(): Int {
            return when (this) {
                LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                DARK -> AppCompatDelegate.MODE_NIGHT_YES
                else -> when {
                    supportsFollowSystem() -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
        }

        @CheckResult
        private fun supportsFollowSystem(): Boolean {
            return Build.VERSION.SDK_INT >= 28
        }
    }

    companion object {

        private val DEFAULT_ON_SET_CALLBACK: (mode: Mode) -> Unit = {}
    }
}
