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
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.billing.BillingPreferences
import com.pyamsoft.pydroid.ui.internal.theme.ThemingPreferences
import com.pyamsoft.pydroid.ui.theme.Theming.Mode
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.SYSTEM
import com.pyamsoft.pydroid.ui.theme.toRawString
import com.pyamsoft.pydroid.ui.theme.toThemingMode
import com.pyamsoft.pydroid.util.booleanFlow
import com.pyamsoft.pydroid.util.intFlow
import com.pyamsoft.pydroid.util.stringFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

internal class PYDroidPreferencesImpl
internal constructor(
    context: Context,
    private val versionCode: Int,
) : ThemingPreferences, BillingPreferences, ChangeLogPreferences, DataPolicyPreferences {

  private val darkModeKey = context.getString(R.string.dark_mode_key)

  private val prefs by lazy {
    Enforcer.assertOffMainThread()
    PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
  }

  override suspend fun listenForUpsellChanges(): Flow<Boolean> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        val countFlow =
            prefs.intFlow(
                KEY_BILLING_SHOW_UPSELL_COUNT,
                DEFAULT_BILLING_SHOW_UPSELL_COUNT,
            )

        return@withContext countFlow
            .map { it >= VALUE_BILLING_SHOW_UPSELL_THRESHOLD }
            .onEach { show ->
              // Once the threshold has been tripped, set it back
              if (show) {
                Logger.d("Reset billing show count!")
                prefs.edit {
                  putInt(
                      KEY_BILLING_SHOW_UPSELL_COUNT,
                      DEFAULT_BILLING_SHOW_UPSELL_COUNT,
                  )
                }
              }
            }
      }

  override suspend fun maybeShowUpsell() =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        val currentCount =
            prefs.getInt(
                KEY_BILLING_SHOW_UPSELL_COUNT,
                DEFAULT_BILLING_SHOW_UPSELL_COUNT,
            )

        if (currentCount < VALUE_BILLING_SHOW_UPSELL_THRESHOLD) {
          prefs.edit { putInt(KEY_BILLING_SHOW_UPSELL_COUNT, currentCount + 1) }
        }
      }

  override suspend fun listenForShowChangelogChanges(): Flow<Boolean> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext prefs
            .intFlow(
                LAST_SHOWN_CHANGELOG,
                DEFAULT_LAST_SHOWN_CHANGELOG_CODE,
            )
            .onEach { lastShown ->
              // Upon the first time seeing it, update to our current version code
              if (lastShown == DEFAULT_LAST_SHOWN_CHANGELOG_CODE) {
                markChangeLogShown()
              }
            }
            .map { it in 1 until versionCode }
      }

  override suspend fun markChangeLogShown() =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        // Mark the changelog as shown for this version
        prefs.edit { putInt(LAST_SHOWN_CHANGELOG, versionCode) }
      }

  override suspend fun listenForDarkModeChanges(): Flow<Mode> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        // Initialize this key here so the preference screen can be populated
        if (!prefs.contains(darkModeKey)) {
          prefs.edit(commit = true) { putString(darkModeKey, DEFAULT_DARK_MODE) }
        }

        return@withContext prefs.stringFlow(darkModeKey, DEFAULT_DARK_MODE).map {
          it.toThemingMode()
        }
      }

  override suspend fun setDarkMode(mode: Mode) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        prefs.edit { putString(darkModeKey, mode.toRawString()) }
      }

  override suspend fun listenForPolicyAcceptedChanges(): Flow<Boolean> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext prefs.booleanFlow(
            KEY_DATA_POLICY_CONSENTED,
            DEFAULT_DATA_POLICY_CONSENTED,
        )
      }

  override suspend fun respondToPolicy(accepted: Boolean) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        prefs.edit { putBoolean(KEY_DATA_POLICY_CONSENTED, accepted) }
      }

  companion object {

    private val DEFAULT_DARK_MODE = SYSTEM.toRawString()

    private const val DEFAULT_LAST_SHOWN_CHANGELOG_CODE = -1
    private const val LAST_SHOWN_CHANGELOG = "changelog_app_last_shown"

    private const val DEFAULT_DATA_POLICY_CONSENTED = false
    private const val KEY_DATA_POLICY_CONSENTED = "data_policy_consented_v1"

    private const val DEFAULT_BILLING_SHOW_UPSELL_COUNT = 0
    private const val KEY_BILLING_SHOW_UPSELL_COUNT = "billing_show_upsell_v1"
    private const val VALUE_BILLING_SHOW_UPSELL_THRESHOLD = 10
  }
}
