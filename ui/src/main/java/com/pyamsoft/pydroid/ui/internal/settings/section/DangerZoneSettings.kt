/*
 * Copyright 2026 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.settings.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters
import com.pyamsoft.pydroid.ui.internal.settings.MutableSettingsViewState
import com.pyamsoft.pydroid.ui.internal.settings.SettingsDangerZoneViewState
import com.pyamsoft.pydroid.ui.internal.settings.section.card.SettingsCard
import com.pyamsoft.pydroid.ui.settings.SimpleSettingsRowItem
import com.pyamsoft.pydroid.ui.settings.SwitchSettingsRowItem

internal fun LazyListScope.renderDangerZoneSettings(
    modifier: Modifier = Modifier,
    state: SettingsDangerZoneViewState,
    onResetClicked: () -> Unit,
    onInAppDebuggingClicked: () -> Unit,
    onInAppDebuggingChanged: (Boolean) -> Unit,
) {
  item {
    val isInAppDebuggingEnabled by state.isInAppDebuggingEnabled.collectAsStateWithLifecycle()

    SettingsCard(
        modifier = modifier.padding(vertical = MaterialTheme.keylines.content),
    ) {
      SwitchSettingsRowItem(
          icon = IconPainters.debugMode(),
          title = stringResource(R.string.dev_mode_title),
          description = stringResource(R.string.dev_mode_summary),
          checked = isInAppDebuggingEnabled,
          onClick = onInAppDebuggingClicked,
          onChange = onInAppDebuggingChanged,
      )

      SimpleSettingsRowItem(
          icon = IconPainters.resetAll(),
          title = stringResource(R.string.clear_all_title),
          description = stringResource(R.string.clear_all_summary),
          onClick = onResetClicked,
      )
    }
  }
}

@Preview
@Composable
private fun PreviewDangerZoneSettings() {
  LazyColumn(
      modifier = Modifier.background(Color.White),
  ) {
    renderDangerZoneSettings(
        state = MutableSettingsViewState(),
        onResetClicked = {},
        onInAppDebuggingChanged = {},
        onInAppDebuggingClicked = {},
    )
  }
}
