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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
import com.pyamsoft.pydroid.ui.util.canUseMaterialYou
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

  private val darkModeKey = stringPreferencesKey(context.getString(R.string.dark_mode_key))

  private val Context.dataStore: DataStore<Preferences> by
      preferencesDataStore(
          name = "pydroid_preferences",
          produceMigrations = {
            listOf(
                SharedPreferencesMigration(
                    produceSharedPreferences = {
                      PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
                    },
                ))
          })

  private val prefs by lazy {
    enforcer.assertOffMainThread()
    context.applicationContext.dataStore
  }

  private val scope by lazy {
    CoroutineScope(
        context = SupervisorJob() + Dispatchers.IO + CoroutineName(this::class.java.name),
    )
  }

  private inline fun setPreference(crossinline block: suspend (MutablePreferences) -> Unit) {
    scope.launch(context = Dispatchers.IO) {
      enforcer.assertOffMainThread()

      prefs.edit { settings ->
        enforcer.assertOffMainThread()
        block(settings)
      }
    }
  }

  private fun setHapticsEnabled(enabled: Boolean) {
    setPreference { it[KEY_HAPTICS_ENABLED] = enabled }
  }

  override fun listenForInAppDebuggingEnabled(): Flow<Boolean> =
      prefs.data
          .map { it[KEY_IN_APP_DEBUGGING] ?: DEFAULT_IN_APP_DEBUGGING_ENABLED }
          .flowOn(context = Dispatchers.IO)

  override fun setInAppDebuggingEnabled(enabled: Boolean) {
    setPreference { it[KEY_IN_APP_DEBUGGING] = enabled }
  }

  override fun listenForBillingUpsellChanges(): Flow<Boolean> =
      prefs.data
          .map { it[KEY_BILLING_SHOW_UPSELL_COUNT] ?: DEFAULT_BILLING_SHOW_UPSELL_COUNT }
          .map { it >= VALUE_BILLING_SHOW_UPSELL_THRESHOLD }
          .flowOn(context = Dispatchers.IO)

  override fun maybeShowBillingUpsell() {
    setPreference { settings ->
      // Get the current count and increment it
      val currentCount =
          prefs.data
              .map { it[KEY_BILLING_SHOW_UPSELL_COUNT] ?: DEFAULT_BILLING_SHOW_UPSELL_COUNT }
              .flowOn(context = Dispatchers.IO)
              .first()

      if (currentCount < VALUE_BILLING_SHOW_UPSELL_THRESHOLD) {
        settings[KEY_BILLING_SHOW_UPSELL_COUNT] = currentCount + 1
      }
    }
  }

  override fun resetBillingShown() {
    setPreference { it[KEY_BILLING_SHOW_UPSELL_COUNT] = DEFAULT_BILLING_SHOW_UPSELL_COUNT }
  }

  override fun listenForShowChangelogChanges(): Flow<Boolean> =
      prefs.data
          .map { it[LAST_SHOWN_CHANGELOG] ?: DEFAULT_LAST_SHOWN_CHANGELOG_CODE }
          .onEach { lastShown ->
            // Upon the first time seeing it, update to our current version code
            if (lastShown == DEFAULT_LAST_SHOWN_CHANGELOG_CODE) {
              Logger.d { "Initialize changelog for a newly installed app!" }
              markChangeLogShown()
            }
          }
          .map { it in 1 until versionCode }
          .flowOn(context = Dispatchers.IO)

  override fun markChangeLogShown() {
    setPreference { it[LAST_SHOWN_CHANGELOG] = versionCode }
  }

  override fun listenForDarkModeChanges(): Flow<Mode> =
      prefs.data
          .map { it[darkModeKey] ?: DEFAULT_DARK_MODE }
          .map { it.toThemingMode() }
          .flowOn(context = Dispatchers.IO)

  override fun setDarkMode(mode: Mode) {
    setPreference { it[darkModeKey] = mode.toRawString() }
  }

  override fun listenForPolicyAcceptedChanges(): Flow<Boolean> =
      prefs.data
          .map { it[KEY_DATA_POLICY_CONSENTED] ?: DEFAULT_DATA_POLICY_CONSENTED }
          .flowOn(context = Dispatchers.IO)

  override fun respondToPolicy(accepted: Boolean) {
    setPreference { it[KEY_DATA_POLICY_CONSENTED] = accepted }
  }

  override fun listenForHapticsChanges(): Flow<Boolean> =
      prefs.data
          .map { it[KEY_HAPTICS_ENABLED] ?: DEFAULT_HAPTICS_ENABLED }
          .flowOn(context = Dispatchers.IO)

  override fun enableHaptics() {
    setHapticsEnabled(true)
  }

  override fun disableHaptics() {
    setHapticsEnabled(false)
  }

  override fun listenForMaterialYouChanges(): Flow<Boolean> =
      prefs.data
          .map { it[KEY_MATERIAL_YOU] ?: DEFAULT_MATERIAL_YOU }
          .flowOn(context = Dispatchers.IO)

  override fun setMaterialYou(enabled: Boolean) {
    setPreference { settings ->
      val isEnabled =
          if (canUseMaterialYou()) {
            enabled
          } else {
            false
          }

      settings[KEY_MATERIAL_YOU] = isEnabled
    }
  }

  companion object {

    private val DEFAULT_DARK_MODE = SYSTEM.toRawString()

    private val KEY_MATERIAL_YOU = booleanPreferencesKey("material_you_v1")
    private const val DEFAULT_MATERIAL_YOU = false

    private val KEY_HAPTICS_ENABLED = booleanPreferencesKey("haptic_manager_v1")
    private const val DEFAULT_HAPTICS_ENABLED = true

    private val LAST_SHOWN_CHANGELOG = intPreferencesKey("changelog_app_last_shown")
    private const val DEFAULT_LAST_SHOWN_CHANGELOG_CODE = -1

    private val KEY_DATA_POLICY_CONSENTED = booleanPreferencesKey("data_policy_consented_v1")
    private const val DEFAULT_DATA_POLICY_CONSENTED = false

    private val KEY_BILLING_SHOW_UPSELL_COUNT = intPreferencesKey("billing_show_upsell_v1")
    private const val DEFAULT_BILLING_SHOW_UPSELL_COUNT = 0
    private const val VALUE_BILLING_SHOW_UPSELL_THRESHOLD = 20

    private val KEY_IN_APP_DEBUGGING = booleanPreferencesKey("in_app_debugging_v1")
    private const val DEFAULT_IN_APP_DEBUGGING_ENABLED = false
  }
}
