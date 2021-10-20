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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.listPreference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.toMode

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
internal fun SettingsScreen(
    state: SettingsViewState,
    onDarkModeChanged: (Theming.Mode) -> Unit,
) {
  val applicationName = state.applicationName
  val darkMode = state.darkMode

  Surface {
    PreferenceScreen(
        preferences =
            listOf(
                applicationPreferences(
                    applicationName = applicationName,
                    darkMode = darkMode,
                    onDarkModeChanged = onDarkModeChanged,
                ),
            ),
    )
  }
}

@Composable
@CheckResult
private fun darkThemePreference(
    darkMode: Theming.Mode,
    onDarkModeChanged: (Theming.Mode) -> Unit,
): Preferences.Item {
  val names = stringArrayResource(R.array.dark_mode_names_v1)
  val values = stringArrayResource(R.array.dark_mode_values_v1)

  return listPreference(
      name = stringResource(R.string.dark_mode_title),
      summary = stringResource(R.string.dark_mode_summary),
      icon = R.drawable.ic_visibility_24dp,
      value = darkMode.toRawString(),
      entries = names.mapIndexed { index, name -> name to values[index] }.toMap(),
      onPreferenceSelected = { _, value -> onDarkModeChanged(value.toMode()) })
}

@Composable
@CheckResult
private fun applicationPreferences(
    applicationName: CharSequence,
    darkMode: Theming.Mode,
    onDarkModeChanged: (Theming.Mode) -> Unit,
): Preferences {
  return preferenceGroup(
      name = "$applicationName Settings",
      preferences =
          listOf(
              darkThemePreference(
                  darkMode = darkMode,
                  onDarkModeChanged = onDarkModeChanged,
              )),
  )
}

@Preview
@Composable
private fun PreviewSettingsScreen() {
  SettingsScreen(
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
