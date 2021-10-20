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

package com.pyamsoft.pydroid.ui.internal.app

import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.ui.R
import com.skydoves.landscapist.coil.CoilImage

@Composable
internal fun DialogToolbar(
    title: String,
    onClose: () -> Unit,
) {
  TopAppBar(
      backgroundColor = MaterialTheme.colors.primary,
      title = {
        Text(
            text = title,
        )
      },
      navigationIcon = {
        IconButton(
            onClick = onClose,
        ) {
          CoilImage(
              modifier = Modifier.size(24.dp),
              imageModel = R.drawable.ic_close_24dp,
          )
        }
      },
  )
}

@Preview
@Composable
private fun PreviewDialogToolbar() {
  DialogToolbar(
      title = "TEST",
      onClose = {},
  )
}