/*
 * Copyright 2025 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.version

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.internal.widget.InterruptCard
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState

@Composable
internal fun VersionUpgradeAvailableScreen(
    modifier: Modifier = Modifier,
    state: VersionCheckViewState,
    onBeginInAppUpdate: (AppUpdateLauncher, Boolean) -> Unit,
) {
  val hapticManager = LocalHapticManager.current

  val launcher by state.launcher.collectAsStateWithLifecycle()
  val progress by state.updateProgressPercent.collectAsStateWithLifecycle()
  val isReady by state.isUpdateReadyToInstall.collectAsStateWithLifecycle()
  val updateErrorDelegate by state.updateError.collectAsStateWithLifecycle()

  val updateError = updateErrorDelegate

  launcher.also { maybeLauncher ->
    // The launcher CAN be null if the user cancels out of a request so do NOT
    // expect rememberNotNull here
    if (maybeLauncher == null) {
      return
    }

    val isUpdateAvailable = remember(maybeLauncher) { maybeLauncher.availableUpdateVersion() > 0 }

    val isVisible =
        remember(
            isReady,
            progress,
            isUpdateAvailable,
        ) {
          !isReady && progress <= 0 && isUpdateAvailable
        }

    // Show if we have a launcher but its not done downloading the update yet
    //
    // The launcher CAN be null if the user cancels out of a request so do NOT
    // expect rememberNotNull here
    InterruptCard(
        modifier = modifier,
        visible = isVisible,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
      ) {
        val newAvailableVersion = maybeLauncher.availableUpdateVersion()
        if (updateError == null) {
          Text(
              text =
                  stringResource(
                      R.string.an_update_to_version_is_available,
                      newAvailableVersion,
                  ),
              style =
                  MaterialTheme.typography.bodyLarge.copy(
                      color = MaterialTheme.colorScheme.primary,
                  ),
          )
        } else {
          Text(
              text =
                  stringResource(
                      R.string.error_downloading_updated_version,
                      newAvailableVersion,
                  ),
              style =
                  MaterialTheme.typography.bodyLarge.copy(
                      color = MaterialTheme.colorScheme.primary,
                  ),
          )

          Text(
              text = updateError.message ?: "An unexpected error occurred.",
              style =
                  MaterialTheme.typography.bodyMedium.copy(
                      color = MaterialTheme.colorScheme.error,
                  ),
          )
        }

        OutlinedButton(
            modifier = Modifier.padding(top = MaterialTheme.keylines.content),
            onClick = {
              hapticManager?.confirmButtonPress()
              onBeginInAppUpdate(
                  maybeLauncher,
                  updateError != null,
              )
            },
        ) {
          Text(
              text =
                  stringResource(
                      if (updateError == null) R.string.download else R.string.download_try_again),
          )
        }
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
        onBeginInAppUpdate = { _, _ -> },
    )
  }
}

@Preview
@Composable
private fun PreviewVersionCheckScreenAvailable() {
  PreviewVersionUpgradeAvailableScreen(
      state =
          MutableVersionCheckViewState().apply {
            launcher.value = AppUpdateLauncher.test(1)
            isUpdateReadyToInstall.value = false
          },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenEmpty() {
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

@Preview
@Composable
private fun PreviewVersionCheckScreenErrorRecover() {
  PreviewVersionUpgradeAvailableScreen(
      state =
          MutableVersionCheckViewState().apply {
            launcher.value = AppUpdateLauncher.test(1)
            isUpdateReadyToInstall.value = false
            updateError.value = UPDATE_FAILED_DOWNLOAD_ERROR
          },
  )
}
