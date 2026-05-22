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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters
import com.pyamsoft.pydroid.ui.internal.settings.SettingsAppViewState
import com.pyamsoft.pydroid.ui.settings.SimpleSettingsRowItem
import com.pyamsoft.pydroid.ui.theme.Theming

@Composable
internal fun ThemePickerSettingsItem(
  modifier: Modifier = Modifier,
  state: SettingsAppViewState,
  onThemeModeChanged: (Theming.Mode) -> Unit,
) {
  val title = stringResource(R.string.theme_mode_title)

  val themeMode by state.themeMode.collectAsStateWithLifecycle()

  SimpleSettingsRowItem(
    icon = IconPainters.themeMode(),
    title = title,
    afterDescription = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
      ) {
        // TODO 3 custom squares representing each theme mode
        //      Highlight selected one

        // TODO far end, checkbox for Material You theming
      }
    }
  )
}