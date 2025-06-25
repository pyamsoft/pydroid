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
import org.jetbrains.annotations.TestOnly

@Composable
internal fun VersionUpgradeCompleteScreen(
    modifier: Modifier = Modifier,
    state: VersionCheckViewState,
    onCompleteUpdate: () -> Unit,
) {
  val launcher by state.launcher.collectAsStateWithLifecycle()
  val isReady by state.isUpdateReadyToInstall.collectAsStateWithLifecycle()

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
          launcher.let { l ->
            if (l == null) {
              // Should basically never happen
              stringResource(R.string.your_upgrade_is_ready)
            } else {
              stringResource(R.string.your_upgrade_to_version_is_ready, l.availableUpdateVersion())
            }
          }

      Text(
          text = text,
          style =
              MaterialTheme.typography.bodyLarge.copy(
                  color = MaterialTheme.colorScheme.primary,
              ),
      )

      Text(
          modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
          text = stringResource(R.string.complete_upgrade),
          style =
              MaterialTheme.typography.bodyMedium.copy(
                  color = MaterialTheme.colorScheme.primary,
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
            text = stringResource(R.string.upgrade),
        )
      }
    }
  }
}

@TestOnly
@Composable
private fun ThePreviewVersionUpgradeCompleteScreen(
    state: MutableVersionCheckViewState,
) {
  VersionUpgradeCompleteScreen(
      state = state,
      onCompleteUpdate = {},
  )
}

@Preview
@Composable
private fun PreviewVersionUpgradeCompleteScreen() {
  ThePreviewVersionUpgradeCompleteScreen(
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
  ThePreviewVersionUpgradeCompleteScreen(
      state =
          MutableVersionCheckViewState().apply {
            launcher.value = null
            isUpdateReadyToInstall.value = true
          },
  )
}
