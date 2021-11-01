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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.app.AppHeader
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

@Composable
@JvmOverloads
internal fun ChangeLogScreen(
    modifier: Modifier = Modifier,
    state: ChangeLogViewState,
    imageLoader: ImageLoader,
    onRateApp: () -> Unit,
    onClose: () -> Unit
) {
  val icon = state.icon
  val name = state.name
  val changeLog = state.changeLog

  Column(
      modifier = modifier,
  ) {
    AppHeader(
        modifier = Modifier.fillMaxWidth(),
        icon = icon,
        name = name,
        imageLoader = imageLoader,
    )

    Surface {
      ChangeLog(
          changeLog = changeLog,
      )

      Actions(
          onRateApp = onRateApp,
          onClose = onClose,
      )
    }
  }
}

@Composable
private fun ChangeLog(changeLog: List<ChangeLogLine>) {
  LazyColumn(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
  ) {
    items(
        items = changeLog,
    ) { line ->
      ChangeLogListItem(
          modifier = Modifier.fillMaxWidth(),
          line = line,
      )
    }
  }
}

@Composable
private fun Actions(
    onRateApp: () -> Unit,
    onClose: () -> Unit,
) {
  Row(modifier = Modifier.padding(16.dp)) {
    TextButton(
        onClick = onRateApp,
    ) {
      Text(
          text = stringResource(R.string.rate_app),
      )
    }
    Spacer(modifier = Modifier.weight(1F))
    TextButton(
        onClick = onClose,
    ) {
      Text(
          text = stringResource(R.string.close),
      )
    }
  }
}

@Composable
private fun PreviewChangeLogScreen(changeLog: List<ChangeLogLine>) {
  val context = LocalContext.current

  ChangeLogScreen(
      state = ChangeLogViewState(icon = 0, name = "TEST", changeLog = changeLog),
      imageLoader = createNewTestImageLoader(context),
      onRateApp = {},
      onClose = {},
  )
}

@Preview
@Composable
private fun PreviewChangeLogScreenEmpty() {
  PreviewChangeLogScreen(changeLog = emptyList())
}

@Preview
@Composable
private fun PreviewChangeLogScreenContent() {
  PreviewChangeLogScreen(
      changeLog =
          listOf(
              ChangeLogLine(
                  ChangeLogLine.Type.CHANGE,
                  "Just a simple Change, Lots of content content content wow"),
              ChangeLogLine(
                  ChangeLogLine.Type.BUGFIX,
                  "Just a text Bugfix, Lots of content content content wow"),
              ChangeLogLine(
                  ChangeLogLine.Type.FEATURE,
                  "Just a new Feature, Lots of content content content wow"),
          ))
}
