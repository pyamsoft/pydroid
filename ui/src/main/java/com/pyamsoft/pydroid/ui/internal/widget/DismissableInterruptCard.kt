/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.haptics.rememberHapticManager

@Composable
internal fun DismissableInterruptCard(
    modifier: Modifier = Modifier,
    text: String,
    buttonText: String,
    show: Boolean,
    onButtonClicked: () -> Unit,
    onDismiss: () -> Unit,
) {
  val hapticManager = rememberHapticManager()

  InterruptCard(
      modifier = modifier,
      visible = show,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
          verticalAlignment = Alignment.Top,
      ) {
        Text(
            modifier = Modifier.weight(1F).padding(MaterialTheme.keylines.content),
            text = text,
            style =
                MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.primary,
                ),
        )

        IconButton(
            onClick = {
              hapticManager.cancelButtonPress()
              onDismiss()
            },
        ) {
          Icon(
              imageVector = Filled.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colors.primary,
          )
        }
      }

      OutlinedButton(
          modifier = Modifier.padding(MaterialTheme.keylines.content),
          onClick = {
            hapticManager.confirmButtonPress()
            onButtonClicked()
          },
      ) {
        Text(
            text = buttonText,
        )
      }
    }
  }
}

@Preview
@Composable
private fun PreviewDismissableInterruptCard() {
  DismissableInterruptCard(
      show = true,
      text = "TEST TEXT",
      buttonText = "BUTTON",
      onDismiss = {},
      onButtonClicked = {},
  )
}
