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

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.HairlineSize
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.internal.version.upgrade.MutableVersionUpgradeViewState
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeViewState
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState

@Composable
internal fun VersionCheckScreen(
    modifier: Modifier = Modifier,
    appName: String,
    versionCheckState: VersionCheckViewState,
    versionUpgradeState: VersionUpgradeViewState,
    onUpgrade: () -> Unit,
) {
  val isUpgradeReady = versionCheckState.isUpdateReadyToInstall
  val isUpgraded = versionUpgradeState.upgraded

  if (!isUpgradeReady || isUpgraded) {
    return
  }

  Column(
      modifier =
          modifier
              .padding(MaterialTheme.keylines.content)
              .border(
                  width = HairlineSize,
                  color = MaterialTheme.colors.primary,
                  shape = MaterialTheme.shapes.medium,
              )
              .padding(MaterialTheme.keylines.content),
  ) {
    Text(
        text = "An update is available to a newer version of $appName!",
        style =
            MaterialTheme.typography.body2.copy(
                color = MaterialTheme.colors.primary,
            ),
    )

    Button(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
        onClick = onUpgrade,
    ) {
      Text(
          text = "Update",
      )
    }
  }
}

@Composable
private fun PreviewVersionCheckScreen(
    checkState: MutableVersionCheckViewState = MutableVersionCheckViewState(),
    upgradeState: MutableVersionUpgradeViewState = MutableVersionUpgradeViewState(),
) {
  Surface {
    VersionCheckScreen(
        appName = "TEST APP",
        versionCheckState = checkState,
        versionUpgradeState = upgradeState,
        onUpgrade = {},
    )
  }
}

@Preview
@Composable
private fun PreviewVersionCheckScreenAvailable() {
  PreviewVersionCheckScreen(
      checkState =
          MutableVersionCheckViewState().apply {
            isUpdateAvailable = true
            isUpdateReadyToInstall = false
          },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotAvailable() {
  PreviewVersionCheckScreen(
      checkState =
          MutableVersionCheckViewState().apply {
            isUpdateAvailable = false
            isUpdateReadyToInstall = false
          },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenReady() {
  PreviewVersionCheckScreen(
      checkState =
          MutableVersionCheckViewState().apply {
            isUpdateAvailable = true
            isUpdateReadyToInstall = true
          },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotReady() {
  PreviewVersionCheckScreen(
      checkState =
          MutableVersionCheckViewState().apply {
            isUpdateAvailable = true
            isUpdateReadyToInstall = false
          },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotUpgraded() {
  PreviewVersionCheckScreen(
      upgradeState = MutableVersionUpgradeViewState().apply { upgraded = false },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenUpgraded() {
  PreviewVersionCheckScreen(
      upgradeState = MutableVersionUpgradeViewState().apply { upgraded = true },
  )
}
