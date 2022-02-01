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

package com.pyamsoft.pydroid.ui.internal.settings.reset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults

@Composable
internal fun ResetScreen(
    modifier: Modifier = Modifier,
    state: ResetViewState,
    onReset: () -> Unit,
    onClose: () -> Unit,
) {
  val reset = state.reset

  Surface(
      modifier = modifier,
      elevation = DialogDefaults.DialogElevation,
  ) {
    Column(modifier = Modifier.padding(MaterialTheme.keylines.content).fillMaxWidth()) {
      Box(
          modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
      ) { Title() }

      Box(
          modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
      ) { Message() }

      Actions(
          isReset = reset,
          onReset = onReset,
          onClose = onClose,
      )
    }
  }
}

@Composable
private fun Title() {
  Text(
      text = "Are you sure?",
      style = MaterialTheme.typography.h4,
  )
}

@Composable
private fun Message() {
  Column {
    Text(
        text = "All saved data will be cleared and all settings reset to default.",
        style = MaterialTheme.typography.body1,
    )

    Box(
        modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
    ) {
      Text(
          text = "The app will act as if you are launching it for the first time.",
          style = MaterialTheme.typography.body1,
      )
    }

    Box(
        modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
    ) {
      Text(
          text = "This cannot be undone.",
          style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
      )
    }
  }
}

@Composable
private fun Actions(
    isReset: Boolean,
    onReset: () -> Unit,
    onClose: () -> Unit,
) {
  Row {
    Spacer(
        modifier = Modifier.weight(1F),
    )
    TextButton(
        onClick = onClose,
        enabled = !isReset,
    ) {
      Text(
          text = "Cancel",
      )
    }
    Box(
        modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
    ) {
      TextButton(
          onClick = onReset,
          enabled = !isReset,
          colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error),
      ) {
        Text(
            text = "Reset",
        )
      }
    }
  }
}

@Composable
private fun PreviewResetScreen(reset: Boolean) {
  ResetScreen(
      state = MutableResetViewState().apply { this.reset = reset },
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
