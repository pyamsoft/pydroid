/*
 * Copyright 2023 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pyamsoft.pydroid.theme.keylines

/** A compose dialog, but hosted inside of a Box so we can pad and scrim it */
@Composable
public fun PaddedDialog(
    properties: DialogProperties = remember { DialogProperties() },
    onDismissRequest: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
  // Capture lambda here
  val handleDismiss by rememberUpdatedState(onDismissRequest)

  Dialog(
      properties = properties,
      onDismissRequest = handleDismiss,
  ) {
    // Box is fullscreen so the content can expand to whatever but we are
    // constrained by the Dialog composable. We add our own click listener
    // because there "is no outside" with a max size box
    Box(
        modifier =
            Modifier.fillMaxSize()
                .clickable(
                    // Only if we have this flag on
                    enabled = properties.dismissOnClickOutside,
                    // Remove the ripple
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                  handleDismiss()
                }
                .padding(MaterialTheme.keylines.content)
                .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
      content()
    }
  }
}
