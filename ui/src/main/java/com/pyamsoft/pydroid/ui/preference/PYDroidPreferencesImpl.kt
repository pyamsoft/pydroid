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
 *
 */

package com.pyamsoft.pydroid.ui.preference

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.bootstrap.rating.RatingPreferences
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.theme.Theming.Mode
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.SYSTEM
import com.pyamsoft.pydroid.ui.theme.ThemingPreferences
import com.pyamsoft.pydroid.ui.theme.toMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PYDroidPreferencesImpl internal constructor(
    context: Context
) : RatingPreferences, ThemingPreferences {

    private val darkModeKey = context.getString(R.string.dark_mode_key)
    private val prefs by lazy {
        Enforcer.assertOffMainThread()
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

    override suspend fun getRatingAcceptedVersion(): Int =
        withContext(context = Dispatchers.Default) {
            Enforcer.assertOffMainThread()
            return@withContext prefs.getInt(
                RATING_ACCEPTED_VERSION, RatingPreferences.DEFAULT_RATING_ACCEPTED_VERSION
            )
        }

    override suspend fun applyRatingAcceptedVersion(version: Int) =
        withContext(context = Dispatchers.Default) {
            Enforcer.assertOffMainThread()
            prefs.edit {
                putInt(RATING_ACCEPTED_VERSION, version)
            }
        }

    override suspend fun initializeDarkMode(onInit: (mode: Mode) -> Unit) =
        withContext(context = Dispatchers.Default) {
            Enforcer.assertOffMainThread()
            if (!prefs.contains(darkModeKey)) {
                val mode = SYSTEM
                prefs.edit { putString(darkModeKey, mode.toRawString()) }
                onInit(mode)
            }
        }

    override suspend fun getDarkMode(): Mode = withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()
        return@withContext requireNotNull(
            prefs.getString(
                darkModeKey,
                SYSTEM.toRawString()
            )
        ).toMode()
    }

    companion object {

        private const val RATING_ACCEPTED_VERSION = "rating_dialog_accepted_version"
    }
}
