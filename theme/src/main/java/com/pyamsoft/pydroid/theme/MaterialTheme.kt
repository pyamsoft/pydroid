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

package com.pyamsoft.pydroid.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/** A Spacing extension on the Material theme */
public val MaterialTheme.keylines: Keylines
  @Composable @ReadOnlyComposable get() = LocalKeylines.current

/** A Material Theme that also knows about Spacing support */
@Composable
public fun MaterialTheme(
    colors: Colors = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    keylines: Keylines = MaterialTheme.keylines,
    content: @Composable () -> Unit
) {
  MaterialTheme(
      colors = colors,
      typography = typography,
      shapes = shapes,
  ) {
    CompositionLocalProvider(
        LocalKeylines provides keylines,
        content = content,
    )
  }
}
