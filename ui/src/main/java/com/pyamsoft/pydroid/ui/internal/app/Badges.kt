/*
 * Copyright 2024 pyamsoft
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.pyamsoft.pydroid.theme.KeylineDefaults
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.theme.HairlineSize

@Composable
internal fun InAppBadge(
    modifier: Modifier = Modifier,
) {
  Badge(
      modifier = modifier,
      text = stringResource(R.string.in_app_badge),
  )
}

private val BADGE_SHAPE = RoundedCornerShape(KeylineDefaults.Typography)

@Composable
private fun Badge(
    modifier: Modifier = Modifier,
    text: String,
) {
  Box(
      modifier =
          modifier
              .background(
                  brush = SolidColor(Color(0xFF4CAF50)),
                  shape = BADGE_SHAPE,
              )
              .padding(
                  horizontal = MaterialTheme.keylines.typography,
                  vertical = HairlineSize,
              ),
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
private fun PreviewInAppBadge() {
  InAppBadge()
}
