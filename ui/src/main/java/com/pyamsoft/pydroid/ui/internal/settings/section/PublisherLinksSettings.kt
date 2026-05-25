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
import com.pyamsoft.pydroid.ui.internal.icons.IconPainters
import com.pyamsoft.pydroid.ui.internal.settings.section.card.SettingsCard
import com.pyamsoft.pydroid.ui.settings.BadgeSettingsRowItem
import com.pyamsoft.pydroid.ui.settings.ExternalLinkBadge

internal fun LazyListScope.renderPublisherLinksSettings(
    modifier: Modifier = Modifier,
    onViewSocialMediaClicked: () -> Unit,
    onViewBlogClicked: () -> Unit,
) {
  item {
    SettingsCard(
        modifier = modifier.padding(top = MaterialTheme.keylines.content),
    ) {
      BadgeSettingsRowItem(
          icon = IconPainters.blog(),
          title = stringResource(R.string.social_media_b_title),
          description = stringResource(R.string.social_media_b_summary),
          onClick = onViewBlogClicked,
          badge = { ExternalLinkBadge() },
      )

      BadgeSettingsRowItem(
          icon = IconPainters.socialMedia(),
          title = stringResource(R.string.social_media_f_title),
          description = stringResource(R.string.social_media_f_summary),
          onClick = onViewSocialMediaClicked,
          badge = { ExternalLinkBadge() },
      )
    }
  }
}

@Preview
@Composable
private fun PreviewPublisherLinksSettings() {
  LazyColumn(
      modifier = Modifier.background(Color.White),
  ) {
    renderPublisherLinksSettings(
        onViewBlogClicked = {},
        onViewSocialMediaClicked = {},
    )
  }
}
