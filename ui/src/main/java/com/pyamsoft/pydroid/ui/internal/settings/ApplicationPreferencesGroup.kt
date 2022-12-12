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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.annotation.CheckResult
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
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
internal fun rememberApplicationPreferencesGroup(
    options: PYDroidActivityOptions,
    hideUpgradeInformation: Boolean,
    applicationName: CharSequence,
    darkMode: Theming.Mode,
    onDarkModeChanged: (Theming.Mode) -> Unit,
    onLicensesClicked: () -> Unit,
    onCheckUpdateClicked: () -> Unit,
    onShowChangeLogClicked: () -> Unit,
): Preferences.Group {

  val darkThemePreference =
      rememberDarkThemePreference(
          darkMode = darkMode,
          onChange = onDarkModeChanged,
      )

  val licensePreference =
      rememberLicensesPreference(
          onClick = onLicensesClicked,
      )

  val updatePreference =
      rememberUpdatePreference(
          onClick = onCheckUpdateClicked,
      )

  val changeLogPreference =
      rememberChangeLogPreference(
          onClick = onShowChangeLogClicked,
      )

  val preferences =
      remember(
          options.disableChangeLog,
          options.disableVersionCheck,
          hideUpgradeInformation,
          darkThemePreference,
          licensePreference,
          updatePreference,
          changeLogPreference,
      ) {
        mutableListOf<Preferences.Item>().apply {
          add(darkThemePreference)
          add(licensePreference)
          if (!options.disableVersionCheck) {
            add(updatePreference)
          }

          if (!options.disableChangeLog && !hideUpgradeInformation) {
            add(changeLogPreference)
          }
        }
      }

  val title = remember(applicationName) { "$applicationName Settings" }
  return remember(
      title,
      preferences,
  ) {
    preferenceGroup(
        name = title,
        preferences = preferences,
    )
  }
}

@Composable
@CheckResult
private fun rememberDarkThemePreference(
    darkMode: Theming.Mode,
    onChange: (Theming.Mode) -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.dark_mode_title)
  val summary = stringResource(R.string.dark_mode_summary)
  val names = stringArrayResource(R.array.dark_mode_names_v1)
  val values = stringArrayResource(R.array.dark_mode_values_v1)
  val rawValue = remember(darkMode) { darkMode.toRawString() }

  return remember(
      name,
      summary,
      names,
      values,
      rawValue,
      onChange,
  ) {
    listPreference(
        name = name,
        summary = summary,
        icon = Icons.Outlined.Visibility,
        value = rawValue,
        entries = names.mapIndexed { index, n -> n to values[index] }.toMap(),
        onPreferenceSelected = { _, value -> onChange(value.toThemingMode()) },
    )
  }
}

@Composable
@CheckResult
private fun rememberLicensesPreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.about_license_title)
  val summary = stringResource(R.string.about_license_summary)

  return remember(
      name,
      summary,
      onClick,
  ) {
    preference(
        name = name,
        summary = summary,
        icon = Icons.Outlined.LibraryBooks,
        onClick = onClick,
    )
  }
}

@Composable
@CheckResult
private fun rememberUpdatePreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.check_version_title)
  val summary = stringResource(R.string.check_version_summary)

  return remember(
      name,
      summary,
      onClick,
  ) {
    preference(
        name = name,
        summary = summary,
        icon = Icons.Outlined.Download,
        onClick = onClick,
    )
  }
}

@Composable
@CheckResult
private fun rememberChangeLogPreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.upgrade_info_title)
  val summary = stringResource(R.string.upgrade_info_summary)

  return remember(
      name,
      summary,
      onClick,
  ) {
    preference(
        name = name,
        summary = summary,
        icon = Icons.Outlined.Whatshot,
        onClick = onClick,
    )
  }
}
