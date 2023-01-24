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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog as ComposeDialog
import androidx.compose.ui.window.DialogProperties

/**
 * Compose Dialog is currently a little buggy.
 *
 * A dialog sizes incorrectly when launched, and does not re-size when for example the orientation
 * of the device changes. You can see this by creating a Dialog composable with a fill-max-width in
 * landscape and then rotating the device to portait.
 *
 * We try to avoid this class of bugs by always having Dialog take up the screen size, and then
 * passing through clicks.
 *
 * The API tries to match the Dialog one
 */
@Composable
@OptIn(ExperimentalComposeUiApi::class)
public fun Dialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
  // Override the properties and do not use the platform width
  val props =
      DialogProperties(
          dismissOnBackPress = properties.dismissOnBackPress,
          dismissOnClickOutside = properties.dismissOnClickOutside,
          securePolicy = properties.securePolicy,
          usePlatformDefaultWidth = false,
          decorFitsSystemWindows = properties.decorFitsSystemWindows,
      )

  ComposeDialog(
      onDismissRequest = onDismissRequest,
      properties = props,
  ) {
    // This is a transparent box which occupies the whole screen size and tracks clicks to dismiss
    // since there is no Dialog "outside". Dialog will handle the back button for us though.
    Box(
        modifier =
            Modifier.fillMaxSize().clickable(enabled = properties.dismissOnClickOutside) {
              onDismissRequest()
            },
        contentAlignment = Alignment.Center,
    ) {
      // This box hosts the actual Dialog content and eats clicks to avoid clicking a dialog causing
      // it to dismiss
      Box(
          modifier =
              Modifier.clickable(
                  // Use this to avoid ripple
                  interactionSource = remember { MutableInteractionSource() },
                  indication = null,
              ) {
                // Intentional nothing
              },
      ) {
        content()
      }
    }
  }
}
