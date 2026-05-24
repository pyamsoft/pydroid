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

package com.pyamsoft.pydroid.ui.internal.settings.newstuff

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.defaults.ListItemDefaults
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters
import com.pyamsoft.pydroid.ui.internal.settings.MutableSettingsViewState
import com.pyamsoft.pydroid.ui.internal.settings.SettingsAppViewState
import com.pyamsoft.pydroid.ui.settings.SimpleSettingsRowItem
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.canUseMaterialYou

@Composable
internal fun ThemePickerSettingsItem(
  modifier: Modifier = Modifier,
  state: SettingsAppViewState,
  onThemeModeChanged: (Theming.Mode) -> Unit,
  onMaterialYouChanged: (Boolean) -> Unit,
) {
  val title = stringResource(R.string.theme_mode_title)

  SimpleSettingsRowItem(
    icon = IconPainters.themeMode(),
    title = title,
    afterDescription = {
      Column {
        ThemeSelectionRow(
          state = state,
          onThemeModeChanged = onThemeModeChanged,
        )
        MaterialYou(
          state = state,
          onMaterialYouChanged = onMaterialYouChanged,
        )
      }
    }
  )
}

@Composable
private fun ThemeSelectionRow(
  modifier: Modifier = Modifier,
  state: SettingsAppViewState,
  onThemeModeChanged: (Theming.Mode) -> Unit,
) {
  val themeMode by state.themeMode.collectAsStateWithLifecycle()

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start,
  ) {
    // TODO far end, checkbox for Material You theming
    ThemeSelection(
      modifier = Modifier
        .padding(end = MaterialTheme.keylines.content)
        .background(
          color = Color.White,
          shape = MaterialTheme.shapes.small,
        ),
      currentMode = themeMode,
      targetMode = Theming.Mode.LIGHT,
      onThemeModeChanged = onThemeModeChanged,
    )

    ThemeSelection(
      modifier = Modifier
        .padding(end = MaterialTheme.keylines.content)
        .background(
          color = Color.Black,
          shape = MaterialTheme.shapes.small,
        ),
      currentMode = themeMode,
      targetMode = Theming.Mode.DARK,
      onThemeModeChanged = onThemeModeChanged,
    )

    ThemeSelection(
      modifier = Modifier
        .padding(end = MaterialTheme.keylines.content)
        .clip(shape = MaterialTheme.shapes.small)
        .drawBehind {
          val width = size.width
          val height = size.height

          val splitTopX = width * 0.80F
          val splitBottomX = width * 0.20F

          // White triangle
          drawPath(
            path = Path().apply {
              moveTo(0F, 0F)
              lineTo(splitTopX, 0F)
              lineTo(splitBottomX, height)
              lineTo(0F, height)
              close()
            },
            color = Color.White
          )

          // Black triangle
          drawPath(
            path = Path().apply {
              moveTo(splitTopX, 0F)
              lineTo(width, 0F)
              lineTo(width, height)
              lineTo(splitBottomX, height)
              close()
            },
            color = Color.Black
          )
        },
      currentMode = themeMode,
      targetMode = Theming.Mode.SYSTEM,
      onThemeModeChanged = onThemeModeChanged,
    )
  }
}

@Composable
private fun ThemeSelection(
  modifier: Modifier = Modifier,
  targetMode: Theming.Mode,
  currentMode: Theming.Mode,
  onThemeModeChanged: (Theming.Mode) -> Unit,
) {
  val isCurrentMode = remember(currentMode, targetMode) { currentMode == targetMode }

  Box(
    modifier = modifier
      .size(ListItemDefaults.DefaultSize)
      .clickable(enabled = !isCurrentMode) {
        onThemeModeChanged(targetMode)
      }
      .border(
        width = if (isCurrentMode) 4.dp else 1.dp,
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.small,
      ),
  )
}

@Composable
private fun MaterialYou(
  modifier: Modifier = Modifier,
  state: SettingsAppViewState,
  onMaterialYouChanged: (Boolean) -> Unit,
) {
  if (!canUseMaterialYou()) {
    return
  }

  val checked by state.isMaterialYou.collectAsStateWithLifecycle()

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Checkbox(
      checked = checked,
      onCheckedChange = onMaterialYouChanged,
    )

    Text(
      style = MaterialTheme.typography.bodySmall,
      text = stringResource(R.string.enable_material_you),
    )
  }
}

@Preview
@Composable
private fun PreviewThemePickerSettingsItem() {
  Column(
    modifier = Modifier.background(color = Color.White),
  ) {
    ThemePickerSettingsItem(
      state = MutableSettingsViewState(),
      onThemeModeChanged = {},
      onMaterialYouChanged = {},
    )
  }
}