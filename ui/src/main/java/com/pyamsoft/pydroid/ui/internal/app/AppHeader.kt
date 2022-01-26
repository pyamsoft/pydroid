/*
 * Copyright 2021 Peter Kenji Yamanaka
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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

private const val ICON_SIZE = 56

@Composable
@JvmOverloads
internal fun AppHeader(
    modifier: Modifier = Modifier,
    icon: Int,
    name: String,
    imageLoader: ImageLoader,
) {
  var textHeight by remember { mutableStateOf(0) }
  val spaceHeight = textHeight / 2

  Box(
      modifier = modifier,
  ) {
    Column(
        modifier = Modifier.matchParentSize(),
    ) {
      Spacer(
          modifier = Modifier.height(spaceHeight.dp).fillMaxWidth(),
      )
      Spacer(
          modifier =
              Modifier.height((ICON_SIZE + spaceHeight).dp)
                  .background(
                      color = MaterialTheme.colors.surface,
                      shape =
                          MaterialTheme.shapes.medium.copy(
                              bottomEnd = ZeroCornerSize,
                              bottomStart = ZeroCornerSize,
                          ),
                  )
                  .fillMaxWidth(),
      )
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Image(
          painter =
              rememberImagePainter(
                  data = icon,
                  imageLoader = imageLoader,
                  builder = { crossfade(true) },
              ),
          contentDescription = "$name Icon",
          modifier = Modifier.size(ICON_SIZE.dp),
      )
      Text(
          text = name,
          style = MaterialTheme.typography.h5,
          onTextLayout = { textHeight = it.size.height },
      )
    }
  }
}

@Preview
@Composable
private fun PreviewAppHeader() {
  AppHeader(
      icon = 0,
      name = "TEST",
      imageLoader = createNewTestImageLoader(),
  )
}
