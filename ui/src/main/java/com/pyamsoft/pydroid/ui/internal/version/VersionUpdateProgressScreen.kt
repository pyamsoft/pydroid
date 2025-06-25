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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.widget.InterruptCard
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState

@Composable
internal fun VersionUpdateProgressScreen(
    modifier: Modifier = Modifier,
    state: VersionCheckViewState,
) {
  val isUpgradeReady by state.isUpdateReadyToInstall.collectAsStateWithLifecycle()
  val progress by state.updateProgressPercent.collectAsStateWithLifecycle()

  val validProgress =
      remember(progress) { if (progress.isNaN()) 0F else progress.coerceAtMost(1.0F) }

  val isVisible =
      remember(
          isUpgradeReady,
          validProgress,
      ) {
        !isUpgradeReady && validProgress > 0 && validProgress <= 1
      }

  InterruptCard(
      modifier = modifier,
      visible = isVisible,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(MaterialTheme.keylines.content),
    ) {
      Text(
          modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
          text = stringResource(R.string.downloading_update),
          style =
              MaterialTheme.typography.bodyMedium.copy(
                  color = MaterialTheme.colorScheme.primary,
              ),
      )

      LinearProgressIndicator(
          modifier = Modifier.fillMaxWidth(),
          progress = { validProgress },
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
      state = MutableVersionCheckViewState().apply { updateProgressPercent.value = 0F },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenGuarded() {
  PreviewVersionUpdateProgress(
      state = MutableVersionCheckViewState().apply { updateProgressPercent.value = Float.NaN },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenHalf() {
  PreviewVersionUpdateProgress(
      state = MutableVersionCheckViewState().apply { updateProgressPercent.value = 0.50F },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckFull() {
  PreviewVersionUpdateProgress(
      state = MutableVersionCheckViewState().apply { updateProgressPercent.value = 1.00F },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckOverflow() {
  PreviewVersionUpdateProgress(
      state = MutableVersionCheckViewState().apply { updateProgressPercent.value = 2.00F },
  )
}

@Preview
@Composable
private fun PreviewVersionCheckReady() {
  PreviewVersionUpdateProgress(
      state =
          MutableVersionCheckViewState().apply {
            updateProgressPercent.value = 0.50F
            isUpdateReadyToInstall.value = true
          },
  )
}
