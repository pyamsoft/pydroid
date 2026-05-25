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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters
import com.pyamsoft.pydroid.ui.internal.settings.MutableSettingsViewState
import com.pyamsoft.pydroid.ui.internal.settings.SettingsUIViewState
import com.pyamsoft.pydroid.ui.internal.settings.section.card.SettingsCard
import com.pyamsoft.pydroid.ui.internal.settings.section.card.ThemePickerSettingsItem
import com.pyamsoft.pydroid.ui.settings.SwitchSettingsRowItem
import com.pyamsoft.pydroid.ui.theme.Theming

internal fun LazyListScope.renderUISettings(
    modifier: Modifier = Modifier,
    state: SettingsUIViewState,
    onThemeModeChanged: (Theming.Mode) -> Unit,
    onMaterialYouChanged: (Boolean) -> Unit,
    onHapticFeedbackChanged: (Boolean) -> Unit,
) {
  item {
    val isHapticFeedbackEnabled by state.isHapticsEnabled.collectAsStateWithLifecycle()

    val hapticDescriptionResId =
        remember(isHapticFeedbackEnabled) {
          if (isHapticFeedbackEnabled) {
            R.string.haptics_summary_on
          } else {
            R.string.haptics_summary_off
          }
        }

    SettingsCard(
        modifier = modifier.padding(top = MaterialTheme.keylines.content),
    ) {
      ThemePickerSettingsItem(
          state = state,
          onThemeModeChanged = onThemeModeChanged,
          onMaterialYouChanged = onMaterialYouChanged,
      )

      SwitchSettingsRowItem(
          icon = IconPainters.hapticFeedback(),
          title = stringResource(R.string.haptics_title),
          description = stringResource(hapticDescriptionResId),
          checked = isHapticFeedbackEnabled,
          onChange = onHapticFeedbackChanged,
          onClick = { onHapticFeedbackChanged(!isHapticFeedbackEnabled) },
      )
    }
  }
}

@Preview
@Composable
private fun PreviewUISettings() {
  LazyColumn(
      modifier = Modifier.background(Color.White),
  ) {
    renderUISettings(
        state = MutableSettingsViewState(),
        onThemeModeChanged = {},
        onMaterialYouChanged = {},
        onHapticFeedbackChanged = {},
    )
  }
}
