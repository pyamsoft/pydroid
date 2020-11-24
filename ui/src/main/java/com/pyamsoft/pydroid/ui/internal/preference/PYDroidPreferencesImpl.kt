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

package com.pyamsoft.pydroid.ui.internal.preference

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogPreferences
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
    context: Context,
    private val versionCode: Int
) : RatingPreferences, ThemingPreferences, ChangeLogPreferences {

    private val darkModeKey = context.getString(R.string.dark_mode_key)
    private val prefs by lazy {
        Enforcer.assertOffMainThread()
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

    override suspend fun showChangelog(): Boolean =
        withContext(context = Dispatchers.IO) {
            Enforcer.assertOffMainThread()

            // If the changelog has not yet been seen
            return@withContext prefs.getInt(LAST_SHOWN_CHANGELOG, 0) < versionCode
        }

    override suspend fun markChangeLogShown() =
        withContext(context = Dispatchers.IO) {
            Enforcer.assertOffMainThread()

            // Mark the changelog as shown for this version
            return@withContext prefs.edit {
                putInt(LAST_SHOWN_CHANGELOG, versionCode)
            }
        }

    override suspend fun showRating(): Boolean =
        withContext(context = Dispatchers.IO) {
            Enforcer.assertOffMainThread()

            // If the rating has already been seen for this one, don't show it
            return@withContext if (prefs.getInt(LAST_SHOWN_RATING, 0) >= versionCode) false else {
                val shown = prefs.getInt(SHOW_RATING, 0)
                val showRating = shown >= SHOW_RATING_AT
                prefs.edit { putInt(SHOW_RATING, shown + 1) }
                showRating
            }
        }

    override suspend fun markRatingShown() = withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        // Mark the rating as seen for this version
        prefs.edit {
            putInt(SHOW_RATING, 0)
            putInt(LAST_SHOWN_RATING, versionCode)
        }
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
        private const val LAST_SHOWN_RATING = "last_shown_rating"

        private const val LAST_SHOWN_CHANGELOG = "last_shown_changelog"
    }
}
