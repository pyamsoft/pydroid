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

package com.pyamsoft.pydroid.ui.internal.version

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
internal fun VersionUpgradeAvailableScreen(
    modifier: Modifier = Modifier,
    state: VersionCheckViewState,
    onUpgrade: () -> Unit,
) {
  VersionCard(
      modifier = modifier,
      visible = state.isUpdateReadyToInstall,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
    ) {
      Text(
          text = "An update is available to version ${state.availableUpdateVersionCode}",
          style =
              MaterialTheme.typography.body2.copy(
                  color = MaterialTheme.colors.primary,
              ),
      )

      OutlinedButton(
          modifier = Modifier.padding(top = MaterialTheme.keylines.content),
          onClick = onUpgrade,
      ) {
        Text(
            text = "Update",
        )
      }
    }
  }
}

@Composable
private fun PreviewVersionCheckScreen(
    state: MutableVersionCheckViewState,
) {
  Surface {
    VersionUpgradeAvailableScreen(
        state = state,
        onUpgrade = {},
    )
  }
}

@Preview
@Composable
private fun PreviewVersionCheckScreenAvailable() {
  PreviewVersionCheckScreen(
      state =
          MutableVersionCheckViewState().apply {
            availableUpdateVersionCode = Random.nextInt(1..100)
            isUpdateReadyToInstall = false
          },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotAvailable() {
  PreviewVersionCheckScreen(
      state =
          MutableVersionCheckViewState().apply {
            availableUpdateVersionCode = AppUpdateLauncher.NO_VALID_UPDATE_VERSION
            isUpdateReadyToInstall = false
          },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenReady() {
  PreviewVersionCheckScreen(
      state =
          MutableVersionCheckViewState().apply {
            availableUpdateVersionCode = Random.nextInt(1..100)
            isUpdateReadyToInstall = true
          },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotReady() {
  PreviewVersionCheckScreen(
      state =
          MutableVersionCheckViewState().apply {
            availableUpdateVersionCode = AppUpdateLauncher.NO_VALID_UPDATE_VERSION
            isUpdateReadyToInstall = false
          },
  )
}
