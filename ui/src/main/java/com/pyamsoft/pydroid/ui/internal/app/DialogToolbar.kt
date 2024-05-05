/*
 * Copyright 2024 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.app

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.theme.ZeroElevation

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun DialogToolbar(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    onClose: () -> Unit,
) {
  val hapticManager = LocalHapticManager.current

  Surface(
      modifier = modifier,
      color = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      shadowElevation = ZeroElevation,
      shape =
          MaterialTheme.shapes.medium.copy(
              bottomEnd = ZeroCornerSize,
              bottomStart = ZeroCornerSize,
          ),
  ) {
    // Must be inside the Surface, since it changes the content color
    val contentColor = LocalContentColor.current

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = contentColor,
                navigationIconContentColor = contentColor,
                actionIconContentColor = contentColor,
            ),
        title = {
          Text(
              text = stringResource(title),
          )
        },
        navigationIcon = {
          IconButton(
              onClick = {
                hapticManager?.cancelButtonPress()
                onClose()
              },
          ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(android.R.string.cancel),
            )
          }
        },
    )
  }
}

@Preview
@Composable
private fun PreviewDialogToolbar() {
  DialogToolbar(
      title = R.string.reset_title,
      onClose = {},
  )
}
