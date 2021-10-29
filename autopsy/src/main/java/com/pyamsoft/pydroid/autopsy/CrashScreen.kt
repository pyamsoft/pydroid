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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
  val scrollState = rememberScrollState()
  val message = remember { stackTrace.message }
  val stackTraceAsString = remember { stackTrace.stackTraceToString() }

  Surface(
      modifier = modifier,
      color = colorResource(R.color.crash_background_color),
      contentColor = colorResource(R.color.crash_foreground_color),
  ) {
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
      Box(
          modifier = Modifier.padding(bottom = 8.dp),
      ) {
        ThreadName(
            threadName = threadName,
        )
      }

      Box(
          modifier = Modifier.padding(bottom = 8.dp),
      ) {
        ThrowableName(
            throwableName = throwableName,
        )
      }

      message?.also { m ->
        Box(
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
          ThrowableMessage(
              message = m,
          )
        }
      }

      StackTrace(
          scrollState = scrollState,
          stackTrace = stackTraceAsString,
      )
    }
  }
}

@Composable
private fun ThreadName(
    threadName: String,
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

@Composable
private fun ThrowableName(
    throwableName: String,
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

@Composable
private fun ThrowableMessage(
    message: String,
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

@Composable
private fun ColumnScope.StackTrace(
    scrollState: ScrollState,
    stackTrace: String,
) {
  Text(
      modifier = Modifier.verticalScroll(scrollState).weight(1F),
      text = stackTrace,
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
