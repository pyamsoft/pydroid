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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.icons.Download
import com.pyamsoft.pydroid.ui.icons.LibraryBooks
import com.pyamsoft.pydroid.ui.icons.Visibility
import com.pyamsoft.pydroid.ui.icons.Whatshot
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.listPreference
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.toRawString
import com.pyamsoft.pydroid.ui.theme.toThemingMode

@Composable
@CheckResult
internal fun createApplicationPreferencesGroup(
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean,
    applicationName: CharSequence,
    darkMode: Theming.Mode,
    onDarkModeChanged: (Theming.Mode) -> Unit,
    onLicensesClicked: () -> Unit,
    onCheckUpdateClicked: () -> Unit,
    onShowChangeLogClicked: () -> Unit,
    onResetClicked: () -> Unit,
): Preferences.Group {
  val preferences =
      listOf(
          darkThemePreference(
              darkMode = darkMode,
              onDarkModeChanged = onDarkModeChanged,
          ),
          licensesPreference(
              onLicensesClicked = onLicensesClicked,
          ),
          updatePreference(
              onCheckUpdateClicked = { onCheckUpdateClicked() },
          ),
      )

  return preferenceGroup(
      name = "$applicationName Settings",
      preferences =
          preferences +
              decideUpgradeInformationPreference(
                  hideUpgradeInformation = hideUpgradeInformation,
                  onShowChangeLogClicked = onShowChangeLogClicked,
              ) +
              decideResetPreference(
                  hideClearAll = hideClearAll,
                  onResetClicked = onResetClicked,
              ),
  )
}

@Composable
@CheckResult
private fun decideUpgradeInformationPreference(
    hideUpgradeInformation: Boolean,
    onShowChangeLogClicked: () -> Unit,
): List<Preferences.Item> {
  return if (hideUpgradeInformation) emptyList()
  else {
    listOf(
        changeLogPreference(
            onShowChangeLogClicked = onShowChangeLogClicked,
        ),
    )
  }
}

@Composable
@CheckResult
private fun decideResetPreference(
    hideClearAll: Boolean,
    onResetClicked: () -> Unit,
): List<Preferences.Item> {
  return if (hideClearAll) emptyList()
  else {
    listOf(
        resetPreference(
            onResetClicked = onResetClicked,
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
      icon = Icons.Outlined.Visibility,
      value = darkMode.toRawString(),
      entries = names.mapIndexed { index, name -> name to values[index] }.toMap(),
      onPreferenceSelected = { _, value -> onDarkModeChanged(value.toThemingMode()) })
}

@Composable
@CheckResult
private fun licensesPreference(
    onLicensesClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = stringResource(R.string.about_license_title),
      summary = stringResource(R.string.about_license_summary),
      icon = Icons.Outlined.LibraryBooks,
      onClick = onLicensesClicked,
  )
}

@Composable
@CheckResult
private fun updatePreference(
    onCheckUpdateClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = stringResource(R.string.check_version_title),
      summary = stringResource(R.string.check_version_summary),
      icon = Icons.Outlined.Download,
      onClick = onCheckUpdateClicked,
  )
}

@Composable
@CheckResult
private fun changeLogPreference(
    onShowChangeLogClicked: () -> Unit,
): Preferences.Item {
  return preference(
      name = stringResource(R.string.upgrade_info_title),
      summary = stringResource(R.string.upgrade_info_summary),
      icon = Icons.Outlined.Whatshot,
      onClick = onShowChangeLogClicked,
  )
}

@Composable
@CheckResult
private fun resetPreference(onResetClicked: () -> Unit): Preferences.Item {
  return preference(
      name = stringResource(R.string.clear_all_title),
      summary = stringResource(R.string.clear_all_summary),
      icon = Icons.Outlined.Warning,
      onClick = onResetClicked,
  )
}
