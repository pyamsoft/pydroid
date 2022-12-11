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
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState

@Composable
internal fun VersionUpdateProgressScreen(
    modifier: Modifier = Modifier,
    state: VersionCheckViewState,
) {
  val isUpgradeReady = state.isUpdateReadyToInstall
  val progress = state.updateProgressPercent
  val validProgress = remember(progress) { if (progress.isNaN()) 0F else progress }

  val isVisible =
      remember(isUpgradeReady, validProgress) { !isUpgradeReady && validProgress >= 100 }

  VersionCard(
      modifier = modifier,
      visible = isVisible,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
          text = "Update Downloading...",
          style =
              MaterialTheme.typography.caption.copy(
                  color =
                      MaterialTheme.colors.primary.copy(
                          alpha = ContentAlpha.medium,
                      ),
              ),
      )

      LinearProgressIndicator(
          progress = validProgress,
      )
    }
  }
}

@Composable
private fun PreviewVersionUpdateProgress(
    state: MutableVersionCheckViewState,
) {
  Surface {
    VersionUpdateProgressScreen(
        state = state,
    )
  }
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNone() {
  PreviewVersionUpdateProgress(
      state = MutableVersionCheckViewState().apply { updateProgressPercent = 0F },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenGuarded() {
  PreviewVersionUpdateProgress(
      state = MutableVersionCheckViewState().apply { updateProgressPercent = Float.NaN },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenHalf() {
  PreviewVersionUpdateProgress(
      state = MutableVersionCheckViewState().apply { updateProgressPercent = 0.50F },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckFull() {
  PreviewVersionUpdateProgress(
      state = MutableVersionCheckViewState().apply { updateProgressPercent = 1.00F },
  )
}
