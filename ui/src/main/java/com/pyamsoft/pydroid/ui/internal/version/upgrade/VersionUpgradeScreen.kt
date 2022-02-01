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
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults

@Composable
internal fun VersionUpgradeScreen(
    modifier: Modifier = Modifier,
    state: VersionUpgradeViewState,
    onUpgrade: () -> Unit,
    onClose: () -> Unit,
) {
  val isUpgraded = state.upgraded

  Surface(
      modifier = modifier,
      elevation = DialogDefaults.DialogElevation,
  ) {
    Column(
        modifier = Modifier.padding(all = MaterialTheme.keylines.content).fillMaxWidth(),
    ) {
      Box(
          modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
      ) { Title() }

      Box(
          modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
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
        modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
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
        onClick = onClose,
        enabled = !isUpgraded,
    ) {
      Text(
          text = "Later",
      )
    }
    Box(
        modifier = Modifier.padding(start = MaterialTheme.keylines.baseline),
    ) {
      TextButton(
          onClick = onUpgrade,
          enabled = !isUpgraded,
          colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error),
      ) {
        Text(
            text = "Restart",
        )
      }
    }
  }
}

@Composable
private fun PreviewVersionUpgradeScreen(upgraded: Boolean) {
  VersionUpgradeScreen(
      state = MutableVersionUpgradeViewState().apply { this.upgraded = upgraded },
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
