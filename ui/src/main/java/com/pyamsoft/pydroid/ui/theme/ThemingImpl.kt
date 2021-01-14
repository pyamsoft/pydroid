/*
 * Copyright 2020 Peter Kenji Yamanaka
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
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Handles getting current dark mode state and setting dark mode state
 */
internal class ThemingImpl internal constructor(preferences: ThemingPreferences) : Theming {

    init {
        // NOTE: We use GlobalScope here because this is an application level thing
        // Maybe its an anti-pattern but I think in controlled use, its okay.
        //
        // https://medium.com/specto/android-startup-tip-dont-use-kotlin-coroutines-a7b3f7176fe5
        //
        // Coroutine start up is slow. What we can do instead is create a handler, which is cheap, and post
        // to the main thread to defer this work until after start up is done
        Handler(Looper.getMainLooper()).post {
            // Now even though this will take work, it will defer until all other handler work is done
            GlobalScope.launch(context = Dispatchers.Default) {
                val mode = preferences.getDarkMode()
                withContext(context = Dispatchers.Main) {
                    setDarkTheme(mode)
                }
            }
        }
    }

    /**
     * Is activity dark mode
     */
    override fun isDarkTheme(activity: Activity): Boolean {
        val uiMode = activity.resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * Set application wide dark mode
     */
    override fun setDarkTheme(mode: Theming.Mode) {
        AppCompatDelegate.setDefaultNightMode(mode.toAppCompatMode())
    }

}
