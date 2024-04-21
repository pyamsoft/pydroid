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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.annotation.CheckResult
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.icons.Download
import com.pyamsoft.pydroid.ui.icons.LibraryBooks
import com.pyamsoft.pydroid.ui.icons.Vibration
import com.pyamsoft.pydroid.ui.icons.Visibility
import com.pyamsoft.pydroid.ui.icons.Whatshot
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.listPreference
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup
import com.pyamsoft.pydroid.ui.preference.switchPreference
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.toRawString
import com.pyamsoft.pydroid.ui.theme.toThemingMode
import com.pyamsoft.pydroid.ui.util.canUseMaterialYou

@Composable
@CheckResult
internal fun rememberApplicationPreferencesGroup(
    options: PYDroidActivityOptions,
    isHapticsEnabled: Boolean,
    hideUpgradeInformation: Boolean,
    applicationName: CharSequence,
    darkMode: Theming.Mode,
    isMaterialYou: Boolean,
    onMaterialYouChange: (Boolean) -> Unit,
    onDarkModeChanged: (Theming.Mode) -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onLicensesClicked: () -> Unit,
    onCheckUpdateClicked: () -> Unit,
    onShowChangeLogClicked: () -> Unit,
): Preferences.Group {

  val darkThemePreference =
      rememberDarkThemePreference(
          darkMode = darkMode,
          isMaterialYou = isMaterialYou,
          onModeChange = onDarkModeChanged,
          onMaterialYouChange = onMaterialYouChange,
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

  val hapticPreferences =
      rememberHapticPreference(
          checked = isHapticsEnabled,
          onChange = onHapticsChanged,
      )

  val hapticManager = LocalHapticManager.current
  val isUsingHapticManager = remember(hapticManager) { hapticManager != null }

  val preferences =
      remember(
          isUsingHapticManager,
          options.disableChangeLog,
          options.disableVersionCheck,
          hideUpgradeInformation,
          darkThemePreference,
          licensePreference,
          updatePreference,
          changeLogPreference,
          hapticPreferences,
      ) {
        mutableListOf<Preferences.Item>().apply {
          add(darkThemePreference)
          if (isUsingHapticManager) {
            add(hapticPreferences)
          }
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
        id = "app_settings",
        name = title,
        preferences = preferences,
    )
  }
}

private const val MATERIAL_YOU = "Enable Material You"

@Composable
@CheckResult
private fun rememberDarkThemePreference(
    darkMode: Theming.Mode,
    isMaterialYou: Boolean,
    onModeChange: (Theming.Mode) -> Unit,
    onMaterialYouChange: (Boolean) -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.dark_mode_title)
  val summary = stringResource(R.string.dark_mode_summary)
  val names = stringArrayResource(R.array.dark_mode_names_v1)
  val values = stringArrayResource(R.array.dark_mode_values_v1)
  val rawValue = remember(darkMode) { darkMode.toRawString() }

  val handleModeChange by rememberUpdatedState(onModeChange)
  val handleMaterialYouChange by rememberUpdatedState(onMaterialYouChange)

  val materialYouCheckboxes =
      remember(isMaterialYou) {
        if (canUseMaterialYou()) {
          mapOf(MATERIAL_YOU to isMaterialYou)
        } else {
          emptyMap()
        }
      }

  return remember(
      name,
      summary,
      names,
      values,
      rawValue,
      isMaterialYou,
      materialYouCheckboxes,
  ) {
    listPreference(
        id = "dark_mode",
        name = name,
        summary = summary,
        icon = Icons.Outlined.Visibility,
        value = rawValue,
        entries = names.mapIndexed { index, n -> n to values[index] }.toMap(),
        checkboxes = materialYouCheckboxes,
        onPreferenceSelected = { key, value ->
          if (key == MATERIAL_YOU) {
            handleMaterialYouChange(value.toBooleanStrict())
          } else {
            handleModeChange(value.toThemingMode())
          }
        },
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

  val handleClick by rememberUpdatedState(onClick)

  return remember(
      name,
      summary,
  ) {
    preference(
        id = "libraries",
        name = name,
        summary = summary,
        icon = Icons.Outlined.LibraryBooks,
        onClick = { handleClick() },
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

  val handleClick by rememberUpdatedState(onClick)

  return remember(
      name,
      summary,
  ) {
    preference(
        id = "in_app_update",
        name = name,
        summary = summary,
        icon = Icons.Outlined.Download,
        onClick = { handleClick() },
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

  val handleClick by rememberUpdatedState(onClick)

  return remember(
      name,
      summary,
  ) {
    preference(
        id = "show_changelog",
        name = name,
        summary = summary,
        icon = Icons.Outlined.Whatshot,
        onClick = { handleClick() },
    )
  }
}

@Composable
@CheckResult
private fun rememberHapticPreference(
    checked: Boolean,
    onChange: (Boolean) -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.haptics_title)
  val summaryOff = stringResource(R.string.haptics_summary_off)
  val summaryOn = stringResource(R.string.haptics_summary_on)

  val handleClick by rememberUpdatedState { onChange(!checked) }

  return remember(
      name,
      checked,
      summaryOff,
      summaryOn,
  ) {
    switchPreference(
        id = "haptic_feedback",
        name = name,
        summary = if (checked) summaryOn else summaryOff,
        icon = Icons.Outlined.Vibration,
        checked = checked,
        onClick = { handleClick() },
        onCheckedChanged = { handleClick() },
    )
  }
}
