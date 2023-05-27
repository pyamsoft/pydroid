/*
 * Copyright 2023 pyamsoft
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
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.billing.BillingPreferences
import com.pyamsoft.pydroid.ui.internal.debug.DebugPreferences
import com.pyamsoft.pydroid.ui.internal.theme.ThemingPreferences
import com.pyamsoft.pydroid.ui.theme.Theming.Mode
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.SYSTEM
import com.pyamsoft.pydroid.ui.theme.toRawString
import com.pyamsoft.pydroid.ui.theme.toThemingMode
import com.pyamsoft.pydroid.util.preferenceBooleanFlow
import com.pyamsoft.pydroid.util.preferenceIntFlow
import com.pyamsoft.pydroid.util.preferenceStringFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

internal class PYDroidPreferencesImpl
internal constructor(
    enforcer: ThreadEnforcer,
    context: Context,
    private val versionCode: Int,
) :
    ThemingPreferences,
    BillingPreferences,
    ChangeLogPreferences,
    DataPolicyPreferences,
    DebugPreferences {

  private val darkModeKey = context.getString(R.string.dark_mode_key)

  private val prefs by lazy {
    enforcer.assertOffMainThread()
    PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
  }

  private suspend fun incrementToThreshold(
      key: String,
      defaultValue: Int,
      threshold: Int,
  ) =
      withContext(context = Dispatchers.IO) {
        val currentCount = prefs.getInt(key, defaultValue)
        if (currentCount < threshold) {
          prefs.edit { putInt(key, currentCount + 1) }
        }
      }

  override fun listenForInAppDebuggingEnabled(): Flow<Boolean> =
      preferenceBooleanFlow(
              KEY_IN_APP_DEBUGGING,
              DEFAULT_IN_APP_DEBUGGING_ENABLED,
          ) {
            prefs
          }
          .flowOn(context = Dispatchers.IO)

  override suspend fun setInAppDebuggingEnabled(enabled: Boolean) =
      withContext(context = Dispatchers.IO) {
        prefs.edit { putBoolean(KEY_IN_APP_DEBUGGING, enabled) }
      }

  override fun listenForBillingUpsellChanges(): Flow<Boolean> =
      preferenceIntFlow(
              KEY_BILLING_SHOW_UPSELL_COUNT,
              DEFAULT_BILLING_SHOW_UPSELL_COUNT,
          ) {
            prefs
          }
          .map { it >= VALUE_BILLING_SHOW_UPSELL_THRESHOLD }
          .flowOn(context = Dispatchers.IO)

  override suspend fun maybeShowBillingUpsell() =
      withContext(context = Dispatchers.IO) {
        incrementToThreshold(
            KEY_BILLING_SHOW_UPSELL_COUNT,
            DEFAULT_BILLING_SHOW_UPSELL_COUNT,
            VALUE_BILLING_SHOW_UPSELL_THRESHOLD,
        )
      }

  override suspend fun resetBillingShown() =
      withContext(context = Dispatchers.IO) {
        prefs.edit {
          putInt(
              KEY_BILLING_SHOW_UPSELL_COUNT,
              DEFAULT_BILLING_SHOW_UPSELL_COUNT,
          )
        }
      }

  override fun listenForShowChangelogChanges(): Flow<Boolean> =
      preferenceIntFlow(
              LAST_SHOWN_CHANGELOG,
              DEFAULT_LAST_SHOWN_CHANGELOG_CODE,
          ) {
            prefs
          }
          .onEach { lastShown ->
            // Upon the first time seeing it, update to our current version code
            if (lastShown == DEFAULT_LAST_SHOWN_CHANGELOG_CODE) {
              Logger.d("Initialize changelog for a newly installed app!")
              markChangeLogShown()
            }
          }
          .map { it in 1 until versionCode }
          .flowOn(context = Dispatchers.IO)

  override suspend fun markChangeLogShown() =
      withContext(context = Dispatchers.IO) {
        // Mark the changelog as shown for this version
        prefs.edit { putInt(LAST_SHOWN_CHANGELOG, versionCode) }
      }

  override fun listenForDarkModeChanges(): Flow<Mode> =
      preferenceStringFlow(darkModeKey, DEFAULT_DARK_MODE) { prefs }
          .map { it.toThemingMode() }
          .flowOn(context = Dispatchers.IO)

  override suspend fun setDarkMode(mode: Mode) =
      withContext(context = Dispatchers.IO) {
        prefs.edit { putString(darkModeKey, mode.toRawString()) }
      }

  override fun listenForPolicyAcceptedChanges(): Flow<Boolean> =
      preferenceBooleanFlow(
              KEY_DATA_POLICY_CONSENTED,
              DEFAULT_DATA_POLICY_CONSENTED,
          ) {
            prefs
          }
          .flowOn(context = Dispatchers.IO)

  override suspend fun respondToPolicy(accepted: Boolean) =
      withContext(context = Dispatchers.IO) {
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

    private const val DEFAULT_IN_APP_DEBUGGING_ENABLED = false
    private const val KEY_IN_APP_DEBUGGING = "in_app_debugging_v1"
  }
}
