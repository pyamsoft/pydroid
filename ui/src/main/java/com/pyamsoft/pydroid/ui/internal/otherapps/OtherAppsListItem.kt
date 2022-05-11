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

package com.pyamsoft.pydroid.ui.internal.otherapps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import coil.compose.AsyncImage
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.defaults.CardDefaults
import com.pyamsoft.pydroid.ui.defaults.ImageDefaults
import com.pyamsoft.pydroid.ui.internal.app.AdBadge
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

@Composable
internal fun OtherAppsListItem(
    modifier: Modifier = Modifier,
    app: OtherApp,
    imageLoader: ImageLoader,
    onOpenStore: (OtherApp) -> Unit,
    onViewSource: (OtherApp) -> Unit,
) {
  Card(
      modifier = modifier,
      shape = MaterialTheme.shapes.medium,
      elevation = CardDefaults.Elevation,
  ) {
    Row(
        modifier = Modifier.padding(MaterialTheme.keylines.baseline).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
          app = app,
          imageLoader = imageLoader,
      )

      Column(
          modifier = Modifier.padding(start = MaterialTheme.keylines.content).weight(1F),
      ) {
        Name(
            app = app,
        )
        Description(
            app = app,
        )
        Row {
          ViewSource(
              onClick = { onViewSource(app) },
          )
          Box(
              modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
          ) {
            OpenStore(
                onClick = { onOpenStore(app) },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun Name(
    modifier: Modifier = Modifier,
    app: OtherApp,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
        text = app.name,
    )
    Box(
        modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
    ) { AdBadge() }
    Spacer(modifier = Modifier.weight(1F))
  }
}

@Composable
private fun Icon(
    modifier: Modifier = Modifier,
    app: OtherApp,
    imageLoader: ImageLoader,
) {
  val name = app.name
  val icon = app.icon

  if (icon.isNotBlank()) {
    Box(
        modifier = modifier.padding(start = MaterialTheme.keylines.baseline),
        contentAlignment = Alignment.Center,
    ) {
      AsyncImage(
          model = app.icon,
          imageLoader = imageLoader,
          contentDescription = name,
          modifier = Modifier.size(ImageDefaults.DefaultSize),
      )
    }
  }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun Description(
    modifier: Modifier = Modifier,
    app: OtherApp,
) {
  val description = app.description

  AnimatedVisibility(
      modifier = modifier,
      visible = description.isNotBlank(),
  ) {
    Box(
        modifier = Modifier.padding(vertical = MaterialTheme.keylines.baseline),
    ) {
      Text(
          style = MaterialTheme.typography.caption,
          text = description,
      )
    }
  }
}

@Composable
private fun ViewSource(modifier: Modifier = Modifier, onClick: () -> Unit) {
  TextButton(
      modifier = modifier,
      onClick = onClick,
  ) {
    Text(
        text = stringResource(R.string.view_source),
    )
  }
}

@Composable
private fun OpenStore(modifier: Modifier = Modifier, onClick: () -> Unit) {
  Box(
      modifier = modifier.padding(start = MaterialTheme.keylines.baseline),
  ) {
    TextButton(
        onClick = onClick,
    ) {
      Text(
          text = stringResource(R.string.open_store),
      )
    }
  }
}

@Preview
@Composable
private fun PreviewOtherAppsListItem() {
  OtherAppsListItem(
      app =
          OtherApp(
              packageName = "test",
              name = "Test App",
              description = "Just a test app",
              icon =
                  "https://raw.githubusercontent.com/pyamsoft/android-project-versions/master/pasterino.png",
              storeUrl = "some_url",
              sourceUrl = "some_url",
          ),
      imageLoader = createNewTestImageLoader(),
      onOpenStore = {},
      onViewSource = {},
  )
}
