/*
 * Copyright 2022 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.internal.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.theme.SpacingDefaults

@Composable
internal fun AdBadge(
    modifier: Modifier = Modifier,
) {
  Badge(
      modifier = modifier,
      text = stringResource(R.string.ad_badge),
  )
}

@Composable
internal fun InAppBadge(
    modifier: Modifier = Modifier,
) {
  Badge(
      modifier = modifier,
      text = stringResource(R.string.in_app_badge),
  )
}

private val BADGE_SHAPE = RoundedCornerShape(com.pyamsoft.pydroid.theme.SpacingDefaults.Adjustment)

@Composable
private fun Badge(
    modifier: Modifier = Modifier,
    text: String,
) {
  Box(
      modifier =
          modifier
              .background(
                  brush = SolidColor(colorResource(R.color.green500)),
                  shape = BADGE_SHAPE,
              )
              .padding(horizontal = 4.dp, vertical = 1.dp),
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = text,
        style =
            MaterialTheme.typography.caption.copy(
                fontSize = 8.sp,
                color = Color.White,
            ),
    )
  }
}

@Preview
@Composable
private fun PreviewAdBadge() {
  AdBadge()
}
