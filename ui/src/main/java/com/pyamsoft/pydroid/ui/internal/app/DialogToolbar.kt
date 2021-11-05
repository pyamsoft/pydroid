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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

@Composable
internal fun DialogToolbar(
    modifier: Modifier = Modifier,
    title: String,
    imageLoader: ImageLoader,
    onClose: () -> Unit,
) {
  TopAppBar(
      modifier = modifier,
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
          Image(
              painter =
                  rememberImagePainter(
                      data = R.drawable.ic_close_24dp,
                      imageLoader = imageLoader,
                      builder = { crossfade(true) },
                  ),
              contentDescription = stringResource(R.string.close),
              modifier = Modifier.size(24.dp),
          )
        }
      },
  )
}

@Preview
@Composable
private fun PreviewDialogToolbar() {
  val context = LocalContext.current

  DialogToolbar(
      title = "TEST",
      onClose = {},
      imageLoader = createNewTestImageLoader(context),
  )
}
