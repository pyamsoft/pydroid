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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.app.AppHeaderDialog
import com.pyamsoft.pydroid.ui.internal.app.dialogItem
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

@Composable
@JvmOverloads
internal fun ChangeLogScreen(
    modifier: Modifier = Modifier,
    state: ChangeLogDialogViewState,
    imageLoader: ImageLoader,
    onRateApp: () -> Unit,
    onClose: () -> Unit
) {
  AppHeaderDialog(
      modifier = modifier.fillMaxWidth(),
      icon = state.icon,
      name = state.name,
      imageLoader = imageLoader,
  ) {
    state.changeLog.forEach { line ->
      dialogItem(
          modifier = Modifier.fillMaxWidth(),
      ) {
        ChangeLogListItem(
            modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.keylines.content),
            line = line,
        )
      }
    }

    dialogItem(
        modifier = Modifier.fillMaxWidth(),
    ) {
      Actions(
          modifier = Modifier.fillMaxWidth(),
          applicationVersionCode = state.applicationVersionCode,
          onRateApp = onRateApp,
          onClose = onClose,
      )
    }
  }
}

@Composable
private fun Actions(
    modifier: Modifier = Modifier,
    applicationVersionCode: Int,
    onRateApp: () -> Unit,
    onClose: () -> Unit,
) {
  val versionStyle =
      MaterialTheme.typography.overline.copy(
          color =
              MaterialTheme.colors.onSurface.copy(
                  alpha = ContentAlpha.disabled,
              ),
      )

  Row(
      modifier =
          modifier
              .padding(horizontal = MaterialTheme.keylines.content)
              .padding(top = MaterialTheme.keylines.content),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
  ) {
    TextButton(
        onClick = onRateApp,
    ) {
      Text(
          text = stringResource(R.string.rate_app),
      )
    }

    Spacer(modifier = Modifier.weight(1F))

    Text(
        text = "$applicationVersionCode",
        style = versionStyle,
    )

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
  ChangeLogScreen(
      state =
          MutableChangeLogDialogViewState().apply {
            icon = 0
            name = "TEST"
            this.changeLog = changeLog
          },
      imageLoader = createNewTestImageLoader(),
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
