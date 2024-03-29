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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
import com.pyamsoft.pydroid.ui.defaults.ImageDefaults
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader
import com.pyamsoft.pydroid.ui.internal.widget.BetterSurface

internal enum class AppHeaderContentTypes {
  HEADER,
  FOOTER,
  ITEM
}

@Composable
private fun AppHeader(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    name: String,
    imageLoader: ImageLoader,
) {
  val elevation = LocalDialogElevation.current
  val color = LocalDialogColor.current

  Box(
      modifier = modifier,
      contentAlignment = Alignment.BottomCenter,
  ) {
    // Behind the content
    // Space half the height and draw the header behind it
    BetterSurface(
        modifier = Modifier.fillMaxWidth().height(ImageDefaults.LargeSize / 2),
        elevation = elevation,
        color = color,
        shape =
            MaterialTheme.shapes.medium.copy(
                bottomStart = ZeroCornerSize,
                bottomEnd = ZeroCornerSize,
            ),
        content = {},
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
      AsyncImage(
          modifier = Modifier.size(ImageDefaults.LargeSize),
          model = icon,
          imageLoader = imageLoader,
          contentDescription = "$name Icon",
      )
    }
  }
}

private fun noLocalProvidedFor(name: String): Nothing {
  error("CompositionLocal $name not present")
}

/** Dialog elevation */
private val LocalDialogElevation =
    compositionLocalOf<Dp> { noLocalProvidedFor("LocalDialogElevation") }

/** Dialog color */
private val LocalDialogColor = compositionLocalOf<Color> { noLocalProvidedFor("LocalDialogColor") }

@Composable
internal fun AppHeaderDialog(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    name: String,
    imageLoader: ImageLoader,
    color: Color = MaterialTheme.colors.surface,
    content: LazyListScope.() -> Unit,
) {
  CompositionLocalProvider(
      LocalDialogElevation provides DialogDefaults.Elevation,
      LocalDialogColor provides color,
  ) {
    LazyColumn(
        modifier = modifier,
    ) {
      val scope = this
      item(
          contentType = AppHeaderContentTypes.HEADER,
      ) {
        AppHeader(
            modifier = Modifier.fillMaxWidth(),
            icon = icon,
            name = name,
            imageLoader = imageLoader,
        )
      }

      scope.content()

      // Footer for dialogs
      item(
          contentType = AppHeaderContentTypes.FOOTER,
      ) {
        val elevation = LocalDialogElevation.current
        val c = LocalDialogColor.current

        BetterSurface(
            modifier = Modifier.fillMaxWidth().height(MaterialTheme.keylines.baseline),
            elevation = elevation,
            color = c,
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
}

@Composable
private inline fun RenderDialogContent(
    modifier: Modifier,
    crossinline content: @Composable () -> Unit,
) {
  val elevation = LocalDialogElevation.current
  val color = LocalDialogColor.current

  BetterSurface(
      modifier = modifier,
      elevation = elevation,
      color = color,
      shape = RectangleShape,
  ) {
    content()
  }
}

/** Wraps a LazyListScope.item in a Surface so it appears in the Dialog correctly */
internal inline fun LazyListScope.dialogItem(
    modifier: Modifier = Modifier,
    contentType: Any = AppHeaderContentTypes.ITEM,
    crossinline content: @Composable () -> Unit
) {
  val self = this
  self.item(
      contentType = contentType,
  ) {
    RenderDialogContent(
        modifier = modifier,
        content = content,
    )
  }
}

/** Wraps a LazyListScope.item in a Surface so it appears in the Dialog correctly */
internal inline fun <T> LazyListScope.dialogItems(
    modifier: Modifier = Modifier,
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { AppHeaderContentTypes.ITEM },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
  val self = this
  self.items(
      items = items,
      key = key,
      contentType = contentType,
  ) { item ->
    RenderDialogContent(
        modifier = modifier,
    ) {
      itemContent(item)
    }
  }
}

/** Wraps a LazyListScope.item in a Surface so it appears in the Dialog correctly */
internal inline fun <T> LazyListScope.dialogItems(
    modifier: Modifier = Modifier,
    items: Array<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline contentType: (item: T) -> Any? = { AppHeaderContentTypes.ITEM },
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
  val self = this
  self.items(
      items = items,
      key = key,
      contentType = contentType,
  ) { item ->
    RenderDialogContent(
        modifier = modifier,
    ) {
      itemContent(item)
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
    dialogItem {
      Text(
          text = "Just a Test",
      )
    }
  }
}
