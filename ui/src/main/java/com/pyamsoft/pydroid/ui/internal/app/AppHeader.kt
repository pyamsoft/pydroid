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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import coil.compose.AsyncImage
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.ImageDefaults
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

@Composable
internal fun AppHeader(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    name: String,
    imageLoader: ImageLoader,
    afterScroll: @Composable ColumnScope.() -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
  Box(
      modifier = modifier,
      contentAlignment = Alignment.TopCenter,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
      Spacer(
          modifier = Modifier.height(ImageDefaults.LargeSize / 2),
      )
      Card(
          modifier = Modifier.fillMaxWidth(),
          elevation = CardDefaults.elevatedCardElevation(),
          colors = CardDefaults.elevatedCardColors(),
          shape = MaterialTheme.shapes.medium,
      ) {
        Spacer(
            modifier = Modifier.height(ImageDefaults.LargeSize / 2),
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
          content()
        }

        afterScroll()
      }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
      AsyncImage(
          modifier = Modifier.size(ImageDefaults.LargeSize),
          model = icon,
          imageLoader = imageLoader,
          contentDescription = "$name Icon",
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
  ) {
    item {
      Text(
          modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
          text = "Just a Test",
      )
    }
  }
}
