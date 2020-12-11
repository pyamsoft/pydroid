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
import androidx.annotation.CheckResult
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

    @CheckResult
    private fun formatCalendar(calendar: Calendar): String {
        val formatter = requireNotNull(lastShownDateFormatter.get())
        return requireNotNull(formatter.format(calendar.time))
    }

    @CheckResult
    private fun parseCalendar(string: String): Calendar {
        val formatter = requireNotNull(lastShownDateFormatter.get())
        val date = requireNotNull(formatter.parse(string))
        return Calendar.getInstance().apply { time = date }
    }

    @CheckResult
    private fun getDefaultLastSeenDateString(): String {
        // Grab a date that positions the app to notify in a week
        val tenDaysAgo = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
            add(Calendar.WEEK_OF_MONTH, 1)
        }
        return formatCalendar(tenDaysAgo)
    }

    @CheckResult
    private fun getLastSeenDate(defaultDateString: String): Calendar {
        val prefString = prefs.getString(LAST_SHOWN_RATING_DATE, defaultDateString)
        val lastSeenDateAsString = requireNotNull(prefString)
        return parseCalendar(lastSeenDateAsString)
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

            val defaultDateString = getDefaultLastSeenDateString()

            // Initialize the last shown date to ten days ago
            if (!prefs.contains(LAST_SHOWN_RATING_DATE)) {
                Timber.d("Initialize last shown date to: $defaultDateString")
                prefs.edit { putString(LAST_SHOWN_RATING_DATE, defaultDateString) }
            }

            val lastSeenCalendar = getLastSeenDate(defaultDateString)

            // Make sure it has been at least a month since we have last seen the review dialog
            // If it has been at least a month, then when we add the date to last seen it will still be before today
            val adjustedLastSeenCalendar = Calendar.getInstance().apply {
                time = lastSeenCalendar.time
                add(Calendar.MONTH, 1)
            }

            val today = Calendar.getInstance()
            Timber.d("Last show stats")
            Timber.d("Last seen date: ${lastSeenCalendar.time}")
            Timber.d("Adjusted date: ${adjustedLastSeenCalendar.time}")
            Timber.d("Today : ${today.time}")

            return@withContext forceShowRating || adjustedLastSeenCalendar.before(today)
        }

    override suspend fun markRatingShown() = withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        val today = Calendar.getInstance()
        val todayAsString = formatCalendar(today)

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
