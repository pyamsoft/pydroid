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

package com.pyamsoft.pydroid.ui.internal.preference

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogPreferences
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyPreferences
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.theme.Theming.Mode
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.SYSTEM
import com.pyamsoft.pydroid.ui.theme.ThemingPreferences
import com.pyamsoft.pydroid.ui.theme.toRawString
import com.pyamsoft.pydroid.ui.theme.toThemingMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PYDroidPreferencesImpl
internal constructor(
    context: Context,
    private val versionCode: Int,
) : ThemingPreferences, ChangeLogPreferences, DataPolicyPreferences {

  private val darkModeKey = context.getString(R.string.dark_mode_key)
  private val prefs by lazy {
    Enforcer.assertOffMainThread()
    PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
  }

  override suspend fun showChangelog(): Boolean =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        // If the changelog has not yet been seen
        val lastShown = prefs.getInt(LAST_SHOWN_CHANGELOG, -1)
        if (lastShown < 0) {
          return@withContext false
        }
        return@withContext lastShown < versionCode
      }

  override suspend fun markChangeLogShown() =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        // Mark the changelog as shown for this version
        return@withContext prefs.edit { putInt(LAST_SHOWN_CHANGELOG, versionCode) }
      }

  override suspend fun getDarkMode(): Mode =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        // Initialize this key here so the preference screen can be populated
        if (!prefs.contains(darkModeKey)) {
          prefs.edit(commit = true) { putString(darkModeKey, DEFAULT_DARK_MODE) }
        }

        return@withContext prefs
            .getString(darkModeKey, DEFAULT_DARK_MODE)
            .requireNotNull()
            .toThemingMode()
      }

  override suspend fun setDarkMode(mode: Mode) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext prefs.edit(commit = true) { putString(darkModeKey, mode.toRawString()) }
      }

  override suspend fun isPolicyAccepted(): Boolean =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext prefs.getBoolean(
            KEY_DATA_POLICY_CONSENTED, DEFAULT_DATA_POLICY_CONSENTED)
      }

  override suspend fun respondToPolicy(accepted: Boolean) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext prefs.edit(commit = true) {
          putBoolean(KEY_DATA_POLICY_CONSENTED, accepted)
        }
      }

  companion object {

    private val DEFAULT_DARK_MODE = SYSTEM.toRawString()
    private const val LAST_SHOWN_CHANGELOG = "changelog_app_last_shown"

    private const val DEFAULT_DATA_POLICY_CONSENTED = false
    private const val KEY_DATA_POLICY_CONSENTED = "data_policy_consented_v1"
  }
}
