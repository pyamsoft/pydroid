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

package com.pyamsoft.pydroid.autopsy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.keylines

private enum class CrashScreenContentTypes {
  THREAD,
  THROWABLE,
  MESSAGE,
  TRACE,
}

@Composable
internal fun CrashScreen(
    modifier: Modifier = Modifier,
    threadName: String,
    throwableName: String,
    throwableMessage: String,
    stackTrace: String,
) {
  Surface(
      modifier = modifier,
      color = Color(0xFF880000),
      contentColor = Color(0xFFFFFF66),
  ) {
    LazyColumn(
        modifier = Modifier.padding(MaterialTheme.keylines.typography),
    ) {
      item(
          contentType = CrashScreenContentTypes.THREAD,
      ) {
        ThreadName(
            threadName = threadName,
        )
      }

      item(
          contentType = CrashScreenContentTypes.THROWABLE,
      ) {
        ThrowableName(
            throwableName = throwableName,
        )
      }

      item(
          contentType = CrashScreenContentTypes.MESSAGE,
      ) {
        ThrowableMessage(
            throwableMessage = throwableMessage,
        )
      }

      item(
          contentType = CrashScreenContentTypes.TRACE,
      ) {
        StackTrace(
            stackTrace = stackTrace,
        )
      }
    }
  }
}

@Composable
private fun ThreadName(
    modifier: Modifier = Modifier,
    threadName: String,
) {
  Box(
      modifier = modifier.padding(bottom = MaterialTheme.keylines.baseline),
  ) {
    Text(
        text = "Uncaught exception in $threadName thread",
        style =
            MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.W700,
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
      modifier = modifier.padding(bottom = MaterialTheme.keylines.baseline),
  ) {
    Text(
        text = throwableName,
        style =
            MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.W700,
                fontFamily = FontFamily.Monospace,
            ),
    )
  }
}

@Composable
private fun ThrowableMessage(
    modifier: Modifier = Modifier,
    throwableMessage: String,
) {
  val canShow = remember(throwableMessage) { throwableMessage.isNotBlank() }

  if (canShow) {
    Box(
        modifier = modifier.padding(bottom = MaterialTheme.keylines.baseline),
    ) {
      Text(
          text = throwableMessage,
          style =
              MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = FontWeight.W700,
                  fontFamily = FontFamily.Monospace,
              ),
      )
    }
  }
}

@Composable
private fun StackTrace(
    modifier: Modifier = Modifier,
    stackTrace: String,
) {
  Text(
      modifier = modifier,
      text = stackTrace,
      style =
          MaterialTheme.typography.bodyMedium.copy(
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
      throwableMessage = "TEST ERROR",
      stackTrace = RuntimeException("TEST STACK TRACE").stackTraceToString(),
  )
}
