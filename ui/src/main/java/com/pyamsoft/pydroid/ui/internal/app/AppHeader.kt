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

package com.pyamsoft.pydroid.ui.internal.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
import com.pyamsoft.pydroid.ui.defaults.ImageDefaults
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader
import com.pyamsoft.pydroid.ui.theme.ZeroElevation

@Composable
private fun AppHeader(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    name: String,
    imageLoader: ImageLoader,
    elevation: Dp = ZeroElevation,
    color: Color = MaterialTheme.colors.surface,
) {
  val imageHeight = ImageDefaults.LargeSize
  Box(
      modifier = modifier,
      contentAlignment = Alignment.BottomCenter,
  ) {
    // Behind the content
    // Space half the height and draw the header behind it
    Surface(
        modifier = Modifier.height(imageHeight / 2),
        elevation = elevation,
        color = color,
        shape =
            MaterialTheme.shapes.medium.copy(
                bottomStart = ZeroCornerSize,
                bottomEnd = ZeroCornerSize,
            ),
        content = {},
    )

    AsyncImage(
        model = icon,
        imageLoader = imageLoader,
        contentDescription = "$name Icon",
        modifier = Modifier.size(ImageDefaults.LargeSize),
    )
  }
}

internal interface AppHeaderScope {

  fun item(
      content: @Composable () -> Unit,
  )

  fun item(
      modifier: Modifier,
      content: @Composable () -> Unit,
  )

  fun snackbar(
      content: @Composable () -> Unit,
  )

  fun snackbar(
      modifier: Modifier,
      content: @Composable () -> Unit,
  )
}

private data class AppHeaderScopeImpl(
    private val elevation: Dp,
    private val color: Color,
) : AppHeaderScope {

  private var scope: LazyListScope? = null

  fun applyScope(scope: LazyListScope) {
    this.scope = scope
  }

  fun eraseScope() {
    this.scope = null
  }

  override fun item(content: @Composable () -> Unit) {
    this.item(modifier = Modifier, content = content)
  }

  override fun item(modifier: Modifier, content: @Composable () -> Unit) {
    scope?.item {
      Surface(
          modifier = modifier.padding(horizontal = MaterialTheme.keylines.content),
          elevation = elevation,
          color = color,
          shape = RectangleShape,
          content = content,
      )
    }
  }

  override fun snackbar(content: @Composable () -> Unit) {
    this.snackbar(modifier = Modifier, content = content)
  }

  override fun snackbar(modifier: Modifier, content: @Composable () -> Unit) {
    scope?.item {
      Surface(
          modifier = modifier,
          elevation = elevation,
          color = color,
          shape = RectangleShape,
          content = content,
      )
    }
  }
}

@Composable
internal fun AppHeaderDialog(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    name: String,
    imageLoader: ImageLoader,
    color: Color = MaterialTheme.colors.surface,
    content: AppHeaderScope.() -> Unit,
) {
  val elevation = remember { DialogDefaults.Elevation }
  val appHeaderScope =
      remember(
          elevation,
          color,
      ) {
        AppHeaderScopeImpl(elevation, color)
      }

  DisposableEffect(appHeaderScope) {
    onDispose {
      Logger.d("Erase AppHeader scope on dispose")
      appHeaderScope.eraseScope()
    }
  }

  LazyColumn(
      modifier = modifier,
  ) {
    // Apply the scope for this render pass
    appHeaderScope.applyScope(this)

    item {
      AppHeader(
          elevation = elevation,
          icon = icon,
          name = name,
          imageLoader = imageLoader,
      )
    }

    appHeaderScope.content()

    // Footer for dialogs
    item {
      Surface(
          modifier = Modifier.height(MaterialTheme.keylines.content),
          elevation = elevation,
          color = color,
          shape =
              MaterialTheme.shapes.medium.copy(
                  topStart = ZeroCornerSize,
                  topEnd = ZeroCornerSize,
              ),
          content = {},
      )
    }
  }
}

@Preview
@Composable
private fun PreviewAppHeader() {
  AppHeader(
      icon = 0,
      name = "TEST",
      imageLoader = createNewTestImageLoader(),
  )
}

@Preview
@Composable
private fun PreviewAppHeaderDialog() {
  AppHeaderDialog(
      icon = 0,
      name = "TEST",
      imageLoader = createNewTestImageLoader(),
  ) {
    item {
      Text(
          text = "Just a Test",
      )
    }
  }
}
