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

package com.pyamsoft.pydroid.ui.internal.settings.section.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.defaults.ListItemDefaults
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters
import com.pyamsoft.pydroid.ui.internal.settings.MutableSettingsViewState
import com.pyamsoft.pydroid.ui.internal.settings.SettingsInAppInteractionViewState
import com.pyamsoft.pydroid.ui.settings.BadgeSettingsRowItem
import com.pyamsoft.pydroid.ui.settings.InAppBadge

@Composable
internal fun TipJarSettingsItem(
    modifier: Modifier = Modifier,
    state: SettingsInAppInteractionViewState,
    onDonateClicked: () -> Unit,
    onBillingUpsellDisabledChanged: (Boolean) -> Unit,
) {
  val isBillingUpsellDisabled by state.isBillingUpsellDisabled.collectAsStateWithLifecycle()

  Column(
      modifier = modifier,
  ) {
    BadgeSettingsRowItem(
        icon = IconPainters.tipJar(),
        title = stringResource(R.string.donate_title),
        description = stringResource(R.string.donate_summary),
        onClick = onDonateClicked,
        badge = { InAppBadge() },
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          modifier =
              Modifier.padding(
                  start = ListItemDefaults.LeadingSize + MaterialTheme.keylines.baseline
              ),
          text = stringResource(R.string.billing_upsell_disabled_summary),
          style = MaterialTheme.typography.labelSmall,
      )

      Checkbox(
          checked = isBillingUpsellDisabled,
          onCheckedChange = onBillingUpsellDisabledChanged,
      )
    }
  }
}

@Preview
@Composable
private fun PreviewTipJarSettingsItem() {
  Column(
      modifier = Modifier.background(color = Color.White),
  ) {
    TipJarSettingsItem(
        state = MutableSettingsViewState(),
        onDonateClicked = {},
        onBillingUpsellDisabledChanged = {},
    )
  }
}
