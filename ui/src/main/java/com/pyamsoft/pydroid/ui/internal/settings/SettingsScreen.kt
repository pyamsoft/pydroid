/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.toMode
import com.skydoves.landscapist.coil.CoilImage
import de.schnettler.datastore.compose.model.Preference
import de.schnettler.datastore.compose.ui.PreferenceScreen
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.PreferenceRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
internal fun SettingsScreen(
    dataStore: DataStore<Preferences>,
    state: SettingsViewState,
    onDarkModeChanged: (Theming.Mode) -> Unit,
) {
  val applicationName = state.applicationName
  val darkMode = state.darkMode

  val dataStoreManager = remember {
    DataStoreManager(
        dataStore = dataStore,
    )
  }

  Surface {
    PreferenceScreen(
        items =
            listOf(
                applicationPreferences(
                    dataStoreManager = dataStoreManager,
                    applicationName = applicationName,
                    darkMode = darkMode,
                    onDarkModeChanged = onDarkModeChanged,
                )),
        dataStoreManager = dataStoreManager,
        statusBarPadding = true,
    )
  }
}

@Composable
@CheckResult
private fun darkThemePreference(
    dataStoreManager: DataStoreManager,
    darkMode: Theming.Mode,
    onDarkModeChanged: (Theming.Mode) -> Unit,
): Preference.PreferenceItem<out Any> {
  val key = stringResource(R.string.dark_mode_key)
  val names = stringArrayResource(R.array.dark_mode_names_v1)
  val values = stringArrayResource(R.array.dark_mode_values_v1)

  val request = remember {
    PreferenceRequest(
        key = stringPreferencesKey(key),
        defaultValue = darkMode.toRawString(),
    )
  }

  LaunchedEffect(request) {
    Logger.d("Watching for changes to Dark Mode: ", key)
    dataStoreManager.getPreferenceFlow(request).collect { newDarkMode ->
      val mode = newDarkMode.toMode()
      Logger.d("Dark mode changed: $darkMode -> $mode")
      onDarkModeChanged(mode)
    }
  }

  return Preference.PreferenceItem.ListPreference(
      request = request,
      title = stringResource(R.string.dark_mode_title),
      summary = stringResource(R.string.dark_mode_summary),
      singleLineTitle = true,
      icon = {
        CoilImage(
            modifier = Modifier.size(24.dp),
            imageModel = R.drawable.ic_visibility_24dp,
        )
      },
      entries = names.mapIndexed { index, name -> name to values[index] }.toMap(),
  )
}

@Composable
@CheckResult
private fun applicationPreferences(
    dataStoreManager: DataStoreManager,
    applicationName: CharSequence,
    darkMode: Theming.Mode,
    onDarkModeChanged: (Theming.Mode) -> Unit,
): Preference.PreferenceGroup {
  return Preference.PreferenceGroup(
      title = "$applicationName Settings",
      enabled = true,
      preferenceItems =
          listOf(
              darkThemePreference(
                  dataStoreManager = dataStoreManager,
                  darkMode = darkMode,
                  onDarkModeChanged = onDarkModeChanged,
              )),
  )
}

@Preview
@Composable
private fun PreviewSettingsScreen() {
  SettingsScreen(
      dataStore =
          object : DataStore<Preferences> {
            private val preferences = emptyPreferences()

            override val data: Flow<Preferences> = emptyFlow()

            override suspend fun updateData(
                transform: suspend (t: Preferences) -> Preferences
            ): Preferences {
              return preferences
            }
          },
      state =
          SettingsViewState(
              hideClearAll = false,
              hideUpgradeInformation = false,
              applicationName = "TEST",
              darkMode = Theming.Mode.LIGHT,
              otherApps = emptyList(),
              navigationError = null,
          ),
      onDarkModeChanged = {},
  )
}
