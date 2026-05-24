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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters
import com.pyamsoft.pydroid.ui.settings.BadgeSettingsRowItem
import com.pyamsoft.pydroid.ui.settings.CheckboxSettingsRowItem
import com.pyamsoft.pydroid.ui.settings.ExternalLinkBadge
import com.pyamsoft.pydroid.ui.settings.InAppBadge

internal fun LazyListScope.renderInAppInteractionSettings(
    modifier: Modifier = Modifier,
    options: PYDroidActivityOptions,
    appName: String,
    state: SettingsInAppInteractionViewState,
    onDonateClicked: () -> Unit,
    onOpenMarketPage: () -> Unit,
    onBillingUpsellDisabledChanged: (Boolean) -> Unit,
) {
  item {
    val isBillingUpsellDisabled by state.isBillingUpsellDisabled.collectAsStateWithLifecycle()

    SettingsCard(
        modifier = modifier.padding(top = MaterialTheme.keylines.content),
    ) {
      if (!options.disableRating) {
        BadgeSettingsRowItem(
            icon = IconPainters.rateApp(),
            title = stringResource(R.string.rating_title, appName),
            description = stringResource(R.string.rating_summary),
            onClick = onOpenMarketPage,
            badge = { ExternalLinkBadge() },
        )
      }

      if (!options.disableBilling) {
        BadgeSettingsRowItem(
            icon = IconPainters.tipJar(),
            title = stringResource(R.string.donate_title),
            description = stringResource(R.string.donate_summary),
            onClick = onDonateClicked,
            badge = { InAppBadge() },
        )

        CheckboxSettingsRowItem(
            icon = IconPainters.tipJarDisabled(),
            title = stringResource(R.string.billing_upsell_disabled_title),
            description = stringResource(R.string.billing_upsell_disabled_summary),
            checked = isBillingUpsellDisabled,
            onChange = onBillingUpsellDisabledChanged,
            onClick = { onBillingUpsellDisabledChanged(!isBillingUpsellDisabled) },
        )
      }
    }
  }
}

@Preview
@Composable
private fun PreviewInAppInteractionSettings() {
  LazyColumn(
      modifier = Modifier.background(Color.White),
  ) {
    renderInAppInteractionSettings(
        options = PYDroidActivityOptions(),
        state = MutableSettingsViewState(),
        appName = "TEST",
        onDonateClicked = {},
        onOpenMarketPage = {},
        onBillingUpsellDisabledChanged = {},
    )
  }
}
