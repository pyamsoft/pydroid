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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.annotation.CheckResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.icons.Terminal
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup
import com.pyamsoft.pydroid.ui.preference.switchPreference

@Composable
@CheckResult
internal fun rememberDangerZonePreferencesGroup(
    hideClearAll: Boolean,
    isInAppDebugChecked: Boolean,
    onResetClicked: () -> Unit,
    onInAppDebuggingClicked: () -> Unit,
    onInAppDebuggingChanged: () -> Unit,
): Preferences.Group {

  val resetPreference =
      rememberResetPreference(
          onClick = onResetClicked,
      )

  val developerModePreference =
      rememberDeveloperModePreference(
          checked = isInAppDebugChecked,
          onClick = onInAppDebuggingClicked,
          onChange = onInAppDebuggingChanged,
      )

  val preferences =
      remember(
          hideClearAll,
          resetPreference,
          developerModePreference,
      ) {
        mutableListOf<Preferences.Item>().apply {
          add(developerModePreference)
          if (!hideClearAll) {
            add(resetPreference)
          }
        }
      }

  val title = remember { "Danger Zone" }
  return remember(
      title,
      preferences,
  ) {
    preferenceGroup(
        id = "danger_zone",
        name = title,
        preferences = preferences,
    )
  }
}

@Composable
@CheckResult
private fun rememberResetPreference(
    onClick: () -> Unit,
): Preferences.Item {
  val name = stringResource(R.string.clear_all_title)
  val summary = stringResource(R.string.clear_all_summary)

  val handleClick by rememberUpdatedState(onClick)

  return remember(
      name,
      summary,
  ) {
    preference(
        id = "reset_app",
        name = name,
        summary = summary,
        icon = Icons.Outlined.Warning,
        onClick = { handleClick() },
    )
  }
}

@Composable
@CheckResult
private fun rememberDeveloperModePreference(
    checked: Boolean,
    onClick: () -> Unit,
    onChange: () -> Unit,
): Preferences.Item {
  val name = "Debug Mode"
  val summary = "Enables a mode to view development information. THIS WILL IMPACT PERFORMANCE."

  val handleClick by rememberUpdatedState(onClick)
  val handleChange by rememberUpdatedState(onChange)

  return remember(
      name,
      summary,
      checked,
  ) {
    switchPreference(
        id = "developer_mode",
        name = name,
        summary = summary,
        icon = Icons.Outlined.Terminal,
        checked = checked,
        onClick = { handleClick() },
        onCheckedChanged = { handleChange() },
    )
  }
}
