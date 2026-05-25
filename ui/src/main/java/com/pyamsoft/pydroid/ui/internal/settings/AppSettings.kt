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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters
import com.pyamsoft.pydroid.ui.internal.settings.section.card.SettingsCard
import com.pyamsoft.pydroid.ui.settings.SimpleSettingsRowItem

internal fun LazyListScope.renderAppSettings(
    modifier: Modifier = Modifier,
    options: PYDroidActivityOptions,
    onCheckUpdateClicked: () -> Unit,
    onShowChangeLogClicked: () -> Unit,
) {
  item {
    SettingsCard(
        modifier = modifier.padding(top = MaterialTheme.keylines.content),
    ) {
      if (!options.disableVersionCheck) {
        SimpleSettingsRowItem(
            icon = IconPainters.checkUpdates(),
            title = stringResource(R.string.check_version_title),
            description = stringResource(R.string.check_version_summary),
            onClick = onCheckUpdateClicked,
        )
      }

      if (!options.disableChangeLog) {
        SimpleSettingsRowItem(
            icon = IconPainters.viewChangelog(),
            title = stringResource(R.string.upgrade_info_title),
            description = stringResource(R.string.upgrade_info_summary),
            onClick = onShowChangeLogClicked,
        )
      }
    }
  }
}

@Preview
@Composable
private fun PreviewAppSettings() {
  LazyColumn(
      modifier = Modifier.background(Color.White),
  ) {
    renderAppSettings(
        options = PYDroidActivityOptions(),
        onCheckUpdateClicked = {},
        onShowChangeLogClicked = {},
    )
  }
}
