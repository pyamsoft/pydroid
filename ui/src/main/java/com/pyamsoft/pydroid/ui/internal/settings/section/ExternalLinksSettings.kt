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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters
import com.pyamsoft.pydroid.ui.internal.settings.section.card.SettingsCard
import com.pyamsoft.pydroid.ui.settings.BadgeSettingsRowItem
import com.pyamsoft.pydroid.ui.settings.ExternalLinkBadge
import com.pyamsoft.pydroid.ui.settings.SimpleSettingsRowItem

internal fun LazyListScope.renderExternalLinksSettings(
    modifier: Modifier = Modifier,
    options: PYDroidActivityOptions,
    onLicensesClicked: () -> Unit,
    onBugReportClicked: () -> Unit,
    onViewSourceClicked: () -> Unit,
    onViewDataPolicyClicked: () -> Unit,
    onViewPrivacyPolicyClicked: () -> Unit,
    onViewTermsOfServiceClicked: () -> Unit,
) {
  item {
    SettingsCard(
        modifier = modifier.padding(top = MaterialTheme.keylines.content),
    ) {
      SimpleSettingsRowItem(
          icon = IconPainters.openSourceLicenses(),
          title = stringResource(R.string.about_license_title),
          description = stringResource(R.string.about_license_summary),
          onClick = onLicensesClicked,
      )

      SimpleSettingsRowItem(
          icon = IconPainters.viewSourceCode(),
          title = stringResource(R.string.view_source_title),
          description = stringResource(R.string.view_source_summary),
          onClick = onViewSourceClicked,
      )

      if (!options.disableDataPolicy) {
        SimpleSettingsRowItem(
            icon = IconPainters.viewDataPolicyDisclosure(),
            title = stringResource(R.string.view_data_policy_title),
            description = stringResource(R.string.view_data_policy_summary),
            onClick = onViewDataPolicyClicked,
        )
      }

      BadgeSettingsRowItem(
          icon = IconPainters.bugReport(),
          title = stringResource(R.string.bugreport_title),
          description = stringResource(R.string.bugreport_summary),
          onClick = onBugReportClicked,
          badge = { ExternalLinkBadge() },
      )

      BadgeSettingsRowItem(
          icon = IconPainters.viewPrivacyPolicy(),
          title = stringResource(R.string.view_privacy_title),
          description = stringResource(R.string.view_privacy_summary),
          onClick = onViewPrivacyPolicyClicked,
          badge = { ExternalLinkBadge() },
      )

      BadgeSettingsRowItem(
          icon = IconPainters.viewTermsOfService(),
          title = stringResource(R.string.view_terms_title),
          description = stringResource(R.string.view_terms_summary),
          onClick = onViewTermsOfServiceClicked,
          badge = { ExternalLinkBadge() },
      )
    }
  }
}

@Preview
@Composable
private fun PreviewExternalLinksSettings() {
  LazyColumn(
      modifier = Modifier.background(Color.White),
  ) {
    renderExternalLinksSettings(
        options = PYDroidActivityOptions(),
        onLicensesClicked = {},
        onBugReportClicked = {},
        onViewSourceClicked = {},
        onViewDataPolicyClicked = {},
        onViewPrivacyPolicyClicked = {},
        onViewTermsOfServiceClicked = {},
    )
  }
}
