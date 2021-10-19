/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.version.upgrade

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun VersionUpgradeScreen(
    state: VersionUpgradeViewState,
    onUpgrade: () -> Unit,
    onClose: () -> Unit,
) {
  val isUpgraded = state.upgraded

  Surface {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
    ) {
      Box(
          modifier = Modifier.padding(bottom = 8.dp),
      ) { Title() }

      Box(
          modifier = Modifier.padding(bottom = 8.dp),
      ) { Message() }

      Actions(
          isUpgraded = isUpgraded,
          onUpgrade = onUpgrade,
          onClose = onClose,
      )
    }
  }
}

@Composable
private fun Title() {
  Text(
      text = "Upgrade Available",
      style = MaterialTheme.typography.h4,
  )
}

@Composable
private fun Message() {
  Column {
    Text(
        text = "A new version has been downloaded!",
        style = MaterialTheme.typography.body1,
    )

    Box(
        modifier = Modifier.padding(top = 8.dp),
    ) {
      Text(
          text = "Click to restart the app and upgrade to the latest version!",
          style = MaterialTheme.typography.body1,
      )
    }
  }
}

@Composable
private fun Actions(
    isUpgraded: Boolean,
    onUpgrade: () -> Unit,
    onClose: () -> Unit,
) {
  Row {
    Spacer(
        modifier = Modifier.weight(1F),
    )
    TextButton(
        onClick = onUpgrade,
        enabled = !isUpgraded,
    ) {
      Text(
          text = "Restart",
      )
    }
    Box(
        modifier = Modifier.padding(start = 8.dp),
    ) {
      TextButton(
          onClick = onClose,
          colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error),
      ) {
        Text(
            text = "Later",
        )
      }
    }
  }
}

@Composable
private fun PreviewVersionUpgradeScreen(upgraded: Boolean) {
  VersionUpgradeScreen(
      state = VersionUpgradeViewState(upgraded = upgraded),
      onUpgrade = {},
      onClose = {},
  )
}

@Preview
@Composable
private fun PreviewVersionUpgradeScreenNotUpgraded() {
  PreviewVersionUpgradeScreen(upgraded = false)
}

@Preview
@Composable
private fun PreviewVersionUpgradeScreenUpgraded() {
  PreviewVersionUpgradeScreen(upgraded = true)
}
