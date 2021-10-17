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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.app.AdBadge
import com.skydoves.landscapist.coil.CoilImage

@Composable
internal fun OtherAppsListItem(app: OtherApp, onOpenStore: () -> Unit, onViewSource: () -> Unit) {
  Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(size = 4.dp),
      elevation = 2.dp,
  ) {
    Row(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
      Icon(
          app = app,
      )

      Column(
          modifier = Modifier.padding(start = 8.dp).weight(1F),
      ) {
        Name(
            app = app,
        )
        Description(
            app = app,
        )
        Row {
          ViewSource(
              onClick = onViewSource,
          )
          Box(
              modifier = Modifier.padding(start = 8.dp),
          ) {
            OpenStore(
                onClick = onOpenStore,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun Name(app: OtherApp) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
        text = app.name,
    )
    Box(modifier = Modifier.padding(start = 8.dp)) { AdBadge() }
    Spacer(modifier = Modifier.weight(1F))
  }
}

@Composable
private fun Icon(app: OtherApp) {
  val icon = app.icon

  if (icon.isNotBlank()) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
      CoilImage(
          imageModel = app.icon,
          modifier = Modifier.size(64.dp),
      )
    }
  }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun Description(app: OtherApp) {
  val description = app.description

  AnimatedVisibility(visible = description.isNotBlank()) {
    Box(
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
      Text(
          style = MaterialTheme.typography.caption,
          text = description,
      )
    }
  }
}

@Composable
private fun ViewSource(onClick: () -> Unit) {
  TextButton(
      onClick = onClick,
  ) {
    Text(
        text = stringResource(R.string.view_source),
    )
  }
}

@Composable
private fun OpenStore(onClick: () -> Unit) {
  Box(modifier = Modifier.padding(start = 8.dp)) {
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
      onOpenStore = {},
      onViewSource = {},
  )
}
