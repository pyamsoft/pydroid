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

package com.pyamsoft.pydroid.ui.internal.version

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.internal.widget.InterruptCard
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState

@Composable
internal fun VersionUpgradeCompleteScreen(
    modifier: Modifier = Modifier,
    state: VersionCheckViewState,
    onCompleteUpdate: () -> Unit,
) {
  val launcher by state.launcher.collectAsState()
  val isReady by state.isUpdateReadyToInstall.collectAsState()

  val hapticManager = LocalHapticManager.current

  val isUpdateAvailable =
      remember(launcher) { launcher.let { it != null && it.availableUpdateVersion() > 0 } }

  val isVisible =
      remember(
          isReady,
          isUpdateAvailable,
      ) {
        isReady && isUpdateAvailable
      }

  // Show once the upgrade is ready
  InterruptCard(
      modifier = modifier,
      visible = isVisible,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
    ) {
      val text =
          remember(launcher) {
            launcher.let { l ->
              if (l == null) {
                // Should basically never happen
                "Your in-app update is ready!"
              } else {
                "Your in-app update to version ${l.availableUpdateVersion()} is ready!"
              }
            }
          }

      Text(
          text = text,
          style =
              MaterialTheme.typography.body1.copy(
                  color = MaterialTheme.colors.primary,
              ),
      )

      Text(
          modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
          text = "Completing the in-app update will close and restart this app.",
          style =
              MaterialTheme.typography.caption.copy(
                  color =
                      MaterialTheme.colors.primary.copy(
                          alpha = ContentAlpha.medium,
                      ),
              ),
      )

      OutlinedButton(
          modifier = Modifier.padding(top = MaterialTheme.keylines.content),
          onClick = {
            hapticManager?.confirmButtonPress()
            onCompleteUpdate()
          },
      ) {
        Text(
            text = "Complete Update",
        )
      }
    }
  }
}

@Composable
private fun PreviewVersionUpgradeCompleteScreen(
    state: MutableVersionCheckViewState,
) {
  Surface {
    VersionUpgradeCompleteScreen(
        state = state,
        onCompleteUpdate = {},
    )
  }
}

@Preview
@Composable
private fun PreviewVersionUpgradeCompleteScreen() {
  PreviewVersionUpgradeCompleteScreen(
      state =
          MutableVersionCheckViewState().apply {
            launcher.value = AppUpdateLauncher.empty()
            isUpdateReadyToInstall.value = true
          },
  )
}

@Preview
@Composable
private fun PreviewVersionUpgradeCompleteScreenNoLauncher() {
  PreviewVersionUpgradeCompleteScreen(
      state =
          MutableVersionCheckViewState().apply {
            launcher.value = null
            isUpdateReadyToInstall.value = true
          },
  )
}
