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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.ImageDefaults
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader
import com.pyamsoft.pydroid.ui.theme.ZeroElevation

@Composable
internal fun AppHeader(
    modifier: Modifier = Modifier,
    elevation: Dp = ZeroElevation,
    @DrawableRes icon: Int,
    imageLoader: ImageLoader,
    renderItems: LazyListScope.() -> Unit,
) {
  val (pinnedHeight, setPinnedHeight) = remember { mutableStateOf(ZeroSize) }
  val spaceHeight = remember(pinnedHeight) { pinnedHeight / 2 }

  Box(
      modifier = modifier,
      contentAlignment = Alignment.TopCenter,
  ) {
    // Behind the content
    // Space half the height and draw the header behind it
    Surface(
        modifier = Modifier.padding(top = spaceHeight),
        elevation = elevation,
        shape = MaterialTheme.shapes.medium,
    ) {
      Box(
          modifier = Modifier.padding(top = spaceHeight),
      ) {
        LazyColumn { renderItems() }
      }
    }

    PinnedContent(
        modifier = Modifier.fillMaxWidth(),
        icon = icon,
        imageLoader = imageLoader,
        onMeasured = { setPinnedHeight(it) },
    )
  }
}

@Composable
private fun PinnedContent(
    modifier: Modifier = Modifier,
    icon: Int,
    imageLoader: ImageLoader,
    onMeasured: (Dp) -> Unit,
) {
  val density = LocalDensity.current
  Column(
      modifier =
          modifier
              .padding(horizontal = MaterialTheme.keylines.content)
              .padding(top = MaterialTheme.keylines.content)
              .onSizeChanged { size ->
                val space = size.height / 2
                val height = density.run { space.toDp() }
                onMeasured(height)
              },
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
  ) {
    AsyncImage(
        model = icon,
        imageLoader = imageLoader,
        contentDescription = "App Icon",
        modifier = Modifier.size(ImageDefaults.LargeSize),
    )
  }
}

@Preview
@Composable
private fun PreviewAppHeader() {
  AppHeader(
      icon = 0,
      imageLoader = createNewTestImageLoader(),
  ) {
    item {
      Surface(
          modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
      ) {
        Text(
            text = "Test",
            style = MaterialTheme.typography.body1,
        )
      }
    }
  }
}
