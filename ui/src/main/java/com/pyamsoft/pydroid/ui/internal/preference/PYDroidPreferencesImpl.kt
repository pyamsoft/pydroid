/*
 * Copyright 2025 pyamsoft
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
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogPreferences
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyPreferences
import com.pyamsoft.pydroid.bootstrap.version.fake.FakeUpgradeRequest
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
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.ifNotCancellation
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class PYDroidPreferencesImpl
internal constructor(
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
          corruptionHandler =
              ReplaceFileCorruptionHandler { err ->
                Logger.e(err) { "File corruption detected, start with empty Preferences" }
                return@ReplaceFileCorruptionHandler emptyPreferences()
              },
          produceMigrations = {
            listOf(
                // NOTE(Peter): Since our shared preferences was the DEFAULT process one, loading up
                //              a migration without specifying all keys will also migrate
                //              APPLICATION SPECIFIC PREFERENCES which is what we do NOT want to do.
                //              We instead maintain ONLY a list of the known PYDroid preference keys
                SharedPreferencesMigration(
                    keysToMigrate =
                        setOf(
                            darkModeKey.name,
                            KEY_MATERIAL_YOU.name,
                            KEY_HAPTICS_ENABLED.name,
                            LAST_SHOWN_CHANGELOG.name,
                            KEY_DATA_POLICY_CONSENTED.name,
                            KEY_BILLING_SHOW_UPSELL_COUNT.name,
                            KEY_IN_APP_DEBUGGING.name,
                            DEBUG_KEY_SHOW_CHANGELOG.name,
                            DEBUG_KEY_UPGRADE_AVAILABLE.name,
                            DEBUG_KEY_SHOW_RATING_UPSELL.name,
                            DEBUG_KEY_SHOW_BILLING_UPSELL.name,
                        ),
                    produceSharedPreferences = {
                      PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
                    },
                ),
            )
          },
      )

  private val prefs by lazy { context.applicationContext.dataStore }

  private val scope by lazy {
    CoroutineScope(
        context = SupervisorJob() + Dispatchers.IO + CoroutineName(this::class.java.name),
    )
  }

  private inline fun <T : Any> setPreference(
      key: Preferences.Key<T>,
      fallbackValue: T,
      crossinline value: suspend (Preferences) -> T,
  ) {
    scope.launch(context = Dispatchers.IO) {
      try {
        prefs.edit { it[key] = value(it) }
      } catch (e: Throwable) {
        e.ifNotCancellation { prefs.edit { it[key] = fallbackValue } }
      }
    }
  }

  private fun <T : Any> getPreference(
      key: Preferences.Key<T>,
      value: T,
  ): Flow<T> =
      prefs.data
          .map { it[key] ?: value }
          // Otherwise any time ANY preference updates, ALL preferences will be
          // re-sent
          .distinctUntilChanged()
          .catch { err ->
            Logger.e(err) { "Error reading from dataStore: ${key.name}" }
            prefs.edit { it[key] = value }
            emit(value)
          }

  private fun setHapticsEnabled(enabled: Boolean) {
    setPreference(
        key = KEY_HAPTICS_ENABLED,
        fallbackValue = DEFAULT_HAPTICS_ENABLED,
        value = { enabled },
    )
  }

  override fun listenForInAppDebuggingEnabled(): Flow<Boolean> =
      getPreference(
              key = KEY_IN_APP_DEBUGGING,
              value = DEFAULT_IN_APP_DEBUGGING_ENABLED,
          )
          .flowOn(context = Dispatchers.IO)

  override fun setInAppDebuggingEnabled(enabled: Boolean) {
    setPreference(
        key = KEY_IN_APP_DEBUGGING,
        fallbackValue = DEFAULT_IN_APP_DEBUGGING_ENABLED,
        value = { enabled },
    )
  }

  override fun listenForBillingUpsellChanges(): Flow<Boolean> =
      getPreference(
              key = KEY_BILLING_SHOW_UPSELL_COUNT,
              value = DEFAULT_BILLING_SHOW_UPSELL_COUNT,
          )
          .map { it >= VALUE_BILLING_SHOW_UPSELL_THRESHOLD }
          .flowOn(context = Dispatchers.IO)

  override fun maybeShowBillingUpsell() {
    setPreference(
        key = KEY_BILLING_SHOW_UPSELL_COUNT,
        fallbackValue = DEFAULT_BILLING_SHOW_UPSELL_COUNT,
        value = { settings ->
          // Get the current count and increment it
          val currentCount =
              settings[KEY_BILLING_SHOW_UPSELL_COUNT] ?: DEFAULT_BILLING_SHOW_UPSELL_COUNT

          if (currentCount >= VALUE_BILLING_SHOW_UPSELL_THRESHOLD) {
            return@setPreference currentCount
          }

          return@setPreference currentCount + 1
        },
    )
  }

  override fun resetBillingShown() {
    setPreference(
        key = KEY_BILLING_SHOW_UPSELL_COUNT,
        fallbackValue = DEFAULT_BILLING_SHOW_UPSELL_COUNT,
        value = { DEFAULT_BILLING_SHOW_UPSELL_COUNT },
    )
  }

  override fun listenForShowChangelogChanges(): Flow<Boolean> =
      getPreference(
              key = LAST_SHOWN_CHANGELOG,
              value = DEFAULT_LAST_SHOWN_CHANGELOG_CODE,
          )
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
    setPreference(
        key = LAST_SHOWN_CHANGELOG,
        fallbackValue = DEFAULT_LAST_SHOWN_CHANGELOG_CODE,
        value = { versionCode },
    )
  }

  override fun listenForDarkModeChanges(): Flow<Mode> =
      getPreference(
              key = darkModeKey,
              value = DEFAULT_DARK_MODE,
          )
          .map { it.toThemingMode() }
          .flowOn(context = Dispatchers.IO)

  override fun setDarkMode(mode: Mode) {
    setPreference(
        key = darkModeKey,
        fallbackValue = DEFAULT_DARK_MODE,
        value = { mode.toRawString() },
    )
  }

  override fun listenForPolicyAcceptedChanges(): Flow<Boolean> =
      getPreference(
              key = KEY_DATA_POLICY_CONSENTED,
              value = DEFAULT_DATA_POLICY_CONSENTED,
          )
          .flowOn(context = Dispatchers.IO)

  override fun respondToPolicy(accepted: Boolean) {
    setPreference(
        key = KEY_DATA_POLICY_CONSENTED,
        fallbackValue = DEFAULT_DATA_POLICY_CONSENTED,
        value = { accepted },
    )
  }

  override fun listenForHapticsChanges(): Flow<Boolean> =
      getPreference(
              key = KEY_HAPTICS_ENABLED,
              value = DEFAULT_HAPTICS_ENABLED,
          )
          .flowOn(context = Dispatchers.IO)

  override fun enableHaptics() {
    setHapticsEnabled(true)
  }

  override fun disableHaptics() {
    setHapticsEnabled(false)
  }

  override fun listenForMaterialYouChanges(): Flow<Boolean> =
      getPreference(
              key = KEY_MATERIAL_YOU,
              value = DEFAULT_MATERIAL_YOU,
          )
          .flowOn(context = Dispatchers.IO)

  override fun setMaterialYou(enabled: Boolean) {
    setPreference(
        key = KEY_MATERIAL_YOU,
        fallbackValue = DEFAULT_MATERIAL_YOU,
        value = {
          if (canUseMaterialYou()) {
            enabled
          } else {
            false
          }
        },
    )
  }

  override fun listenUpgradeScenarioAvailable(): Flow<FakeUpgradeRequest> =
      combineTransform(
              listenForInAppDebuggingEnabled(),
              getPreference(
                      DEBUG_KEY_UPGRADE_AVAILABLE,
                      "",
                  )
                  .filterNot { it.isBlank() }
                  .map { FakeUpgradeRequest.valueOf(it) },
          ) { isDebugEnabled, request ->
            if (isDebugEnabled) {
              emit(request)
            }
          }
          .flowOn(context = Dispatchers.IO)

  override fun setUpgradeScenarioAvailable(fake: FakeUpgradeRequest?) {
    setPreference(
        key = DEBUG_KEY_UPGRADE_AVAILABLE,
        fallbackValue = "",
        value = { fake?.name.orEmpty() },
    )
  }

  override fun listenShowChangelogUpsell(): Flow<Boolean> =
      combineTransform(
              listenForInAppDebuggingEnabled(),
              getPreference(DEBUG_KEY_SHOW_CHANGELOG, false),
          ) { isDebugEnabled, show ->
            emit(isDebugEnabled && show)
          }
          .flowOn(context = Dispatchers.IO)

  override fun setShowChangelogUpsell(show: Boolean) {
    setPreference(
        key = DEBUG_KEY_SHOW_CHANGELOG,
        fallbackValue = false,
        value = { show },
    )
  }

  override fun listenTryShowRatingUpsell(): Flow<Boolean> =
      combineTransform(
              listenForInAppDebuggingEnabled(),
              getPreference(DEBUG_KEY_SHOW_RATING_UPSELL, false),
          ) { isDebugEnabled, show ->
            emit(isDebugEnabled && show)
          }
          .flowOn(context = Dispatchers.IO)

  override fun setTryShowRatingUpsell(show: Boolean) {
    setPreference(
        key = DEBUG_KEY_SHOW_RATING_UPSELL,
        fallbackValue = false,
        value = { show },
    )
  }

  override fun listenShowBillingUpsell(): Flow<Boolean> =
      combineTransform(
              listenForInAppDebuggingEnabled(),
              getPreference(DEBUG_KEY_SHOW_BILLING_UPSELL, false),
          ) { isDebugEnabled, show ->
            emit(isDebugEnabled && show)
          }
          .flowOn(context = Dispatchers.IO)

  override fun setShowBillingUpsell(show: Boolean) {
    setPreference(
        key = DEBUG_KEY_SHOW_BILLING_UPSELL,
        fallbackValue = false,
        value = { show },
    )
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

    private val DEBUG_KEY_UPGRADE_AVAILABLE = stringPreferencesKey("debug_upgrade_available_v1")
    private val DEBUG_KEY_SHOW_CHANGELOG = booleanPreferencesKey("debug_show_changelog_v1")
    private val DEBUG_KEY_SHOW_BILLING_UPSELL =
        booleanPreferencesKey("debug_show_billing_upsell_v1")
    private val DEBUG_KEY_SHOW_RATING_UPSELL = booleanPreferencesKey("debug_show_rating_upsell_v1")
  }
}
