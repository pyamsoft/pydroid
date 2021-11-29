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

package com.pyamsoft.pydroid.autopsy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun CrashScreen(
    modifier: Modifier = Modifier,
    threadName: String,
    throwableName: String,
    stackTrace: Throwable,
) {
  Surface(
      modifier = modifier,
      color = colorResource(R.color.crash_background_color),
      contentColor = colorResource(R.color.crash_foreground_color),
  ) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
      ThreadName(
          threadName = threadName,
      )

      ThrowableName(
          throwableName = throwableName,
      )

      ThrowableMessage(
          stackTrace = stackTrace,
      )

      StackTrace(
          modifier = Modifier.weight(1F),
          stackTrace = stackTrace,
      )
    }
  }
}

@Composable
private fun ThreadName(
    modifier: Modifier = Modifier,
    threadName: String,
) {
  Box(
      modifier = modifier.padding(bottom = 8.dp),
  ) {
    Text(
        text = "Uncaught exception in $threadName thread",
        style =
            MaterialTheme.typography.body1.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
    )
  }
}

@Composable
private fun ThrowableName(
    modifier: Modifier = Modifier,
    throwableName: String,
) {
  Box(
      modifier = modifier.padding(bottom = 8.dp),
  ) {
    Text(
        text = throwableName,
        style =
            MaterialTheme.typography.body1.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            ),
    )
  }
}

@Composable
private fun ThrowableMessage(
    modifier: Modifier = Modifier,
    stackTrace: Throwable,
) {
  val message = remember(stackTrace) { stackTrace.message }

  if (message != null) {
    Box(
        modifier = modifier.padding(bottom = 8.dp),
    ) {
      Text(
          text = message,
          style =
              MaterialTheme.typography.body1.copy(
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
              ),
      )
    }
  }
}

@Composable
private fun StackTrace(
    modifier: Modifier = Modifier,
    stackTrace: Throwable,
) {
  val scrollState = rememberScrollState()
  val stackTraceAsString = remember(stackTrace) { stackTrace.stackTraceToString() }

  Text(
      modifier = modifier.verticalScroll(scrollState),
      text = stackTraceAsString,
      style =
          MaterialTheme.typography.body1.copy(
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace,
          ),
  )
}

@Preview
@Composable
private fun PreviewCrashScreen() {
  CrashScreen(
      threadName = "TEST Thread",
      throwableName = "TEST ERROR",
      stackTrace = RuntimeException("TEST ERROR"),
  )
}
