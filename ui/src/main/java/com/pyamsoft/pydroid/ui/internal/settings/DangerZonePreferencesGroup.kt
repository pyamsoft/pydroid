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
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.preference.preference
import com.pyamsoft.pydroid.ui.preference.preferenceGroup

@Composable
@CheckResult
internal fun rememberDangerZonePreferencesGroup(
    hideClearAll: Boolean,
    onResetClicked: () -> Unit,
): Preferences.Group {

  val resetPreference =
      rememberResetPreference(
          onClick = onResetClicked,
      )

  val preferences =
      remember(
          hideClearAll,
          resetPreference,
      ) {
        mutableListOf<Preferences.Item>().apply {
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
        name = name,
        summary = summary,
        icon = Icons.Outlined.Warning,
        onClick = handleClick,
    )
  }
}
