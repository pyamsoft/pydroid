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
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

internal class PYDroidPreferencesImpl internal constructor(
    context: Context,
    private val versionCode: Int,
    private val forceShowRating: Boolean
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
            val lastShownVersion = prefs.getInt(LAST_SHOWN_RATING_VERSION, 0)
            if (lastShownVersion >= versionCode) {
                Timber.d("Last shown version is: $lastShownVersion, we dont need to show.")
                return@withContext false
            }

            // Grab a date ten days ago
            val tenDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, -10)
            }

            val formatter = requireNotNull(lastShownDateFormatter.get())
            val tenDaysAgoAsString = requireNotNull(formatter.format(tenDaysAgo))

            // Initialize the last shown date to ten days ago
            if (!prefs.contains(LAST_SHOWN_RATING_DATE)) {
                Timber.d("Initialize last shown date to: $tenDaysAgoAsString")
                prefs.edit { putString(LAST_SHOWN_RATING_DATE, tenDaysAgoAsString) }
            }

            // Make sure it has been at least a month since we have last seen the review dialog
            val lastSeenDateAsString =
                requireNotNull(prefs.getString(LAST_SHOWN_RATING_DATE, tenDaysAgoAsString))
            val lastSeenDate = requireNotNull(formatter.parse(lastSeenDateAsString))

            // If it has been at least a month, then when we add the date to last seen it will still be before today
            val lastSeenCalendar = Calendar.getInstance().apply {
                time = lastSeenDate
                set(Calendar.MONTH, 1)
            }

            val today = Calendar.getInstance()
            val todayAsString = formatter.format(today.time)

            Timber.d("Last show stats")
            Timber.d("Last seen date: $lastSeenDateAsString")
            Timber.d("Today as string: $todayAsString")

            return@withContext forceShowRating || lastSeenCalendar.before(today)
        }

    override suspend fun markRatingShown() = withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        val formatter = requireNotNull(lastShownDateFormatter.get())
        val today = Calendar.getInstance()
        val todayAsString = formatter.format(today.time)

        Timber.d("Mark today as shown $todayAsString $versionCode")

        // Mark the rating as seen for this version
        prefs.edit {
            putString(LAST_SHOWN_RATING_DATE, todayAsString)
            putInt(LAST_SHOWN_RATING_VERSION, versionCode)
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

        private val lastShownDateFormatter = object : ThreadLocal<DateFormat>() {

            override fun initialValue(): DateFormat {
                return SimpleDateFormat.getDateInstance()
            }
        }

        private const val LAST_SHOWN_RATING_DATE = "rate_app_last_shown_date"
        private const val LAST_SHOWN_RATING_VERSION = "rate_app_last_shown_version"
        private const val LAST_SHOWN_CHANGELOG = "changelog_app_last_shown"
    }
}
