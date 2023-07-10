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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.internal.widget.InterruptCard
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState

@Composable
internal fun VersionUpgradeAvailableScreen(
    modifier: Modifier = Modifier,
    state: VersionCheckViewState,
    onBeginInAppUpdate: (AppUpdateLauncher) -> Unit,
    onShow: () -> Unit,
    onHide: () -> Unit,
) {
  val hapticManager = LocalHapticManager.current

  val launcher by state.launcher.collectAsState()
  val progress by state.updateProgressPercent.collectAsState()
  val isReady by state.isUpdateReadyToInstall.collectAsState()

  val isUpdateAvailable =
      remember(launcher) { launcher.let { it != null && it.availableUpdateVersion() > 0 } }

  val isVisible =
      remember(
          isReady,
          progress,
          isUpdateAvailable,
      ) {
        !isReady && progress <= 0 && isUpdateAvailable
      }

  val handleShow by rememberUpdatedState(onShow)
  val handleHide by rememberUpdatedState(onHide)

  LaunchedEffect(isVisible) {
    if (isVisible) {
      handleShow()
    } else {
      handleHide()
    }
  }

  // Show if we have a launcher but its not done downloading the update yet
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
                "A new in-app update is available!"
              } else {
                "A new in-app update to version ${l.availableUpdateVersion()} is available!"
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

      OutlinedButton(
          modifier = Modifier.padding(top = MaterialTheme.keylines.content),
          onClick = {
            hapticManager?.confirmButtonPress()
            onBeginInAppUpdate(launcher.requireNotNull())
          },
      ) {
        Text(
            text = "Download",
        )
      }
    }
  }
}

@Composable
private fun PreviewVersionUpgradeAvailableScreen(
    state: MutableVersionCheckViewState,
) {
  Surface {
    VersionUpgradeAvailableScreen(
        state = state,
        onBeginInAppUpdate = {},
        onShow = {},
        onHide = {},
    )
  }
}

@Preview
@Composable
private fun PreviewVersionCheckScreenAvailable() {
  PreviewVersionUpgradeAvailableScreen(
      state =
          MutableVersionCheckViewState().apply {
            launcher.value = AppUpdateLauncher.empty()
            isUpdateReadyToInstall.value = false
          },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotAvailable() {
  PreviewVersionUpgradeAvailableScreen(
      state =
          MutableVersionCheckViewState().apply {
            launcher.value = null
            isUpdateReadyToInstall.value = false
          },
  )
}
