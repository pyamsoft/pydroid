/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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
import com.pyamsoft.pydroid.ui.haptics.HapticPreferences
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
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class PYDroidPreferencesImpl
internal constructor(
    private val enforcer: ThreadEnforcer,
    context: Context,
    private val versionCode: Int,
) :
    ThemingPreferences,
    BillingPreferences,
    ChangeLogPreferences,
    DataPolicyPreferences,
    DebugPreferences,
    HapticPreferences {

  private val darkModeKey = context.getString(R.string.dark_mode_key)

  private val prefs by lazy {
    enforcer.assertOffMainThread()
    PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
  }

  private val scope by lazy {
    CoroutineScope(
        context = SupervisorJob() + Dispatchers.Default + CoroutineName(this::class.java.name),
    )
  }

  private fun setHapticsEnabled(enabled: Boolean) {
    scope.launch {
      enforcer.assertOffMainThread()
      prefs.edit { putBoolean(KEY_HAPTICS_ENABLED, enabled) }
    }
  }

  override fun listenForInAppDebuggingEnabled(): Flow<Boolean> =
      preferenceBooleanFlow(
              KEY_IN_APP_DEBUGGING,
              DEFAULT_IN_APP_DEBUGGING_ENABLED,
          ) {
            prefs
          }
          .flowOn(context = Dispatchers.Default)

  override fun setInAppDebuggingEnabled(enabled: Boolean) {
    scope.launch {
      enforcer.assertOffMainThread()
      prefs.edit { putBoolean(KEY_IN_APP_DEBUGGING, enabled) }
    }
  }

  override fun listenForBillingUpsellChanges(): Flow<Boolean> =
      preferenceIntFlow(
              KEY_BILLING_SHOW_UPSELL_COUNT,
              DEFAULT_BILLING_SHOW_UPSELL_COUNT,
          ) {
            prefs
          }
          .map { it >= VALUE_BILLING_SHOW_UPSELL_THRESHOLD }
          .flowOn(context = Dispatchers.Default)

  override fun maybeShowBillingUpsell() {
    scope.launch {
      enforcer.assertOffMainThread()

      val currentCount =
          prefs.getInt(KEY_BILLING_SHOW_UPSELL_COUNT, DEFAULT_BILLING_SHOW_UPSELL_COUNT)
      if (currentCount < VALUE_BILLING_SHOW_UPSELL_THRESHOLD) {
        prefs.edit { putInt(KEY_BILLING_SHOW_UPSELL_COUNT, currentCount + 1) }
      }
    }
  }

  override fun resetBillingShown() {
    scope.launch {
      enforcer.assertOffMainThread()

      prefs.edit {
        putInt(
            KEY_BILLING_SHOW_UPSELL_COUNT,
            DEFAULT_BILLING_SHOW_UPSELL_COUNT,
        )
      }
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
              Logger.d { "Initialize changelog for a newly installed app!" }
              markChangeLogShown()
            }
          }
          .map { it in 1 until versionCode }
          .flowOn(context = Dispatchers.Default)

  override fun markChangeLogShown() {
    scope.launch {
      enforcer.assertOffMainThread()
      // Mark the changelog as shown for this version
      prefs.edit { putInt(LAST_SHOWN_CHANGELOG, versionCode) }
    }
  }

  override fun listenForDarkModeChanges(): Flow<Mode> =
      preferenceStringFlow(darkModeKey, DEFAULT_DARK_MODE) { prefs }
          .map { it.toThemingMode() }
          .flowOn(context = Dispatchers.Default)

  override fun setDarkMode(mode: Mode) {
    scope.launch {
      enforcer.assertOffMainThread()
      prefs.edit { putString(darkModeKey, mode.toRawString()) }
    }
  }

  override fun listenForPolicyAcceptedChanges(): Flow<Boolean> =
      preferenceBooleanFlow(
              KEY_DATA_POLICY_CONSENTED,
              DEFAULT_DATA_POLICY_CONSENTED,
          ) {
            prefs
          }
          .flowOn(context = Dispatchers.Default)

  override fun respondToPolicy(accepted: Boolean) {
    scope.launch {
      enforcer.assertOffMainThread()
      prefs.edit { putBoolean(KEY_DATA_POLICY_CONSENTED, accepted) }
    }
  }

  override fun listenForHapticsChanges(): Flow<Boolean> =
      preferenceBooleanFlow(
              KEY_HAPTICS_ENABLED,
              DEFAULT_HAPTICS_ENABLED,
          ) {
            prefs
          }
          .flowOn(context = Dispatchers.Default)

  override fun enableHaptics() {
    setHapticsEnabled(true)
  }

  override fun disableHaptics() {
    setHapticsEnabled(false)
  }

  companion object {

    private val DEFAULT_DARK_MODE = SYSTEM.toRawString()

    private const val DEFAULT_HAPTICS_ENABLED = true
    private const val KEY_HAPTICS_ENABLED = "haptic_manager_v1"

    private const val DEFAULT_LAST_SHOWN_CHANGELOG_CODE = -1
    private const val LAST_SHOWN_CHANGELOG = "changelog_app_last_shown"

    private const val DEFAULT_DATA_POLICY_CONSENTED = false
    private const val KEY_DATA_POLICY_CONSENTED = "data_policy_consented_v1"

    private const val DEFAULT_BILLING_SHOW_UPSELL_COUNT = 0
    private const val KEY_BILLING_SHOW_UPSELL_COUNT = "billing_show_upsell_v1"
    private const val VALUE_BILLING_SHOW_UPSELL_THRESHOLD = 20

    private const val DEFAULT_IN_APP_DEBUGGING_ENABLED = false
    private const val KEY_IN_APP_DEBUGGING = "in_app_debugging_v1"
  }
}
