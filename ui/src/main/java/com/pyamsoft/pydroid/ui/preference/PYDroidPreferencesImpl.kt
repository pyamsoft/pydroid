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

    override suspend fun showRatingDialog(): Boolean = withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        val shown = prefs.getInt(SHOW_RATING, 0)
        val showRating = shown >= SHOW_RATING_AT
        prefs.edit { putInt(SHOW_RATING, if (showRating) 0 else shown + 1) }
        return@withContext showRating
    }

    override suspend fun getDarkMode(): Mode = withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        return@withContext requireNotNull(
            prefs.getString(
                darkModeKey,
                SYSTEM.toRawString()
            )
        ).toMode()
    }

    companion object {

        private const val SHOW_RATING_AT = 5
        private const val SHOW_RATING = "show_rating"
    }
}
