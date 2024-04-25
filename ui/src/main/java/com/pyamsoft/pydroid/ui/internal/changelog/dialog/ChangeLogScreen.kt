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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.defaults.TypographyDefaults
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.internal.app.AppHeader
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogLine
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader
import com.pyamsoft.pydroid.ui.util.collectAsStateListWithLifecycle

private enum class ChangeLogScreenItems {
  LOG_ITEM,
  ACTIONS,
}

@Composable
@JvmOverloads
internal fun ChangeLogScreen(
    modifier: Modifier = Modifier,
    state: ChangeLogDialogViewState,
    imageLoader: ImageLoader,
    onRateApp: () -> Unit,
    onClose: () -> Unit
) {
  val changeLog = state.changeLog.collectAsStateListWithLifecycle()
  val icon by state.icon.collectAsStateWithLifecycle()
  val name by state.name.collectAsStateWithLifecycle()
  val versionCode by state.applicationVersionCode.collectAsStateWithLifecycle()

  AppHeader(
      modifier = modifier,
      icon = icon,
      name = name,
      imageLoader = imageLoader,
  ) {
    changeLog.forEach { line ->
      item(
          contentType = ChangeLogScreenItems.LOG_ITEM,
      ) {
        ChangeLogListItem(
            modifier = Modifier.fillMaxWidth().padding(horizontal = MaterialTheme.keylines.content),
            line = line,
        )
      }
    }

    item(
        contentType = ChangeLogScreenItems.ACTIONS,
    ) {
      Actions(
          modifier = Modifier.fillMaxWidth(),
          applicationVersionCode = versionCode,
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
  val hapticManager = LocalHapticManager.current
  val versionStyle =
      MaterialTheme.typography.labelSmall.copy(
          color =
              MaterialTheme.colorScheme.onSurface.copy(
                  alpha = TypographyDefaults.ALPHA_DISABLED,
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
        onClick = {
          hapticManager?.confirmButtonPress()
          onRateApp()
        },
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
        onClick = {
          hapticManager?.cancelButtonPress()
          onClose()
        },
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
            this.icon.value = 0
            this.name.value = "TEST"
            this.changeLog.value = changeLog
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
