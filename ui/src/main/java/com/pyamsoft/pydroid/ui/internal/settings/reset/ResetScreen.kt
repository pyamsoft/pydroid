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

package com.pyamsoft.pydroid.ui.internal.settings.reset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager

private enum class ResetScreenContentTypes {
  TITLE,
  MESSAGE,
}

@Composable
internal fun ResetScreen(
    modifier: Modifier = Modifier,
    state: ResetViewState,
    onReset: () -> Unit,
    onClose: () -> Unit,
) {
  val reset by state.reset.collectAsStateWithLifecycle()

  Card(
      modifier = modifier,
      elevation = CardDefaults.elevatedCardElevation(),
      colors = CardDefaults.elevatedCardColors(),
      shape = MaterialTheme.shapes.medium,
  ) {
    LazyColumn(
        modifier =
            Modifier.padding(MaterialTheme.keylines.content)
                .fillMaxWidth()
                .weight(
                    weight = 1F,
                    fill = false,
                ),
    ) {
      item(
          contentType = ResetScreenContentTypes.TITLE,
      ) {
        Box(
            modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
        ) {
          Title()
        }
      }

      item(
          contentType = ResetScreenContentTypes.MESSAGE,
      ) {
        Box(
            modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
        ) {
          Message()
        }
      }
    }

    Actions(
        modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.baseline),
        isReset = reset,
        onReset = onReset,
        onClose = onClose,
    )
  }
}

@Composable
private fun Title() {
  Text(
      text = stringResource(R.string.reset_title),
      style = MaterialTheme.typography.headlineMedium,
  )
}

@Composable
private fun Message() {
  Column {
    Text(
        text = stringResource(R.string.reset_start),
        style = MaterialTheme.typography.bodyLarge,
    )

    Box(
        modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
    ) {
      Text(
          text = stringResource(R.string.reset_end),
          style = MaterialTheme.typography.bodyLarge,
      )
    }

    Box(
        modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
    ) {
      Text(
          text = stringResource(R.string.reset_warning),
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W700),
      )
    }
  }
}

@Composable
private fun Actions(
    modifier: Modifier = Modifier,
    isReset: Boolean,
    onReset: () -> Unit,
    onClose: () -> Unit,
) {
  val hapticManager = LocalHapticManager.current

  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Spacer(
        modifier = Modifier.weight(1F),
    )
    TextButton(
        enabled = !isReset,
        onClick = {
          hapticManager?.cancelButtonPress()
          onClose()
        },
    ) {
      Text(
          text = stringResource(android.R.string.cancel),
      )
    }
    Box(
        modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
    ) {
      TextButton(
          enabled = !isReset,
          colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
          onClick = {
            hapticManager?.confirmButtonPress()
            onReset()
          },
      ) {
        Text(
            text = stringResource(R.string.reset),
        )
      }
    }
  }
}

@Composable
private fun PreviewResetScreen(reset: Boolean) {
  ResetScreen(
      state = MutableResetViewState().apply { this.reset.value = reset },
      onReset = {},
      onClose = {},
  )
}

@Preview
@Composable
private fun PreviewResetScreenNotReset() {
  PreviewResetScreen(reset = false)
}

@Preview
@Composable
private fun PreviewResetScreenReset() {
  PreviewResetScreen(reset = true)
}
