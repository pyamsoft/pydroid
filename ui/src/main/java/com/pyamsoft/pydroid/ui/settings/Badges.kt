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

package com.pyamsoft.pydroid.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R

/** Small font size for small label chippy */
private val BADGE_FONT_SIZE = 8.sp

@Composable
private fun Badge(
    modifier: Modifier = Modifier,
    @StringRes text: Int,
    color: Color,
) {
  Box(
      modifier =
          modifier
              .background(
                  brush = SolidColor(color),
                  shape = MaterialTheme.shapes.small,
              )
              .padding(horizontal = MaterialTheme.keylines.typography),
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = stringResource(text),
        style =
            MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontSize = BADGE_FONT_SIZE,
            ),
    )
  }
}

/** Badge for in-app purchases */
@Composable
public fun InAppBadge(
    modifier: Modifier = Modifier,
) {
  Badge(
      modifier = modifier,
      text = R.string.in_app_badge,
      color = Color(color = 0xFF4CAF50),
  )
}

/** Badge for external links */
@Composable
public fun ExternalLinkBadge(
    modifier: Modifier = Modifier,
) {
  Badge(
      modifier = modifier,
      text = R.string.external_link_badge,
      color = Color(color = 0xFF2196F3),
  )
}

@Preview
@Composable
private fun PreviewInAppBadge() {
  Column(
      modifier = Modifier.background(color = Color.White),
  ) {
    Text(style = MaterialTheme.typography.bodyLarge, text = "Body Large")
    Text(style = MaterialTheme.typography.labelSmall, text = "Label Small")
    InAppBadge()
  }
}

@Preview
@Composable
private fun PreviewExternalLinkBadge() {
  Column(
      modifier = Modifier.background(color = Color.White),
  ) {
    Text(style = MaterialTheme.typography.bodyLarge, text = "Body Large")
    Text(style = MaterialTheme.typography.labelSmall, text = "Label Small")
    ExternalLinkBadge()
  }
}
