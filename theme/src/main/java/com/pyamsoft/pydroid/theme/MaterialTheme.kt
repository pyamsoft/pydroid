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

package com.pyamsoft.pydroid.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

private val GREEN = Color(0xFF4CAF50)
private val AMBER = Color(0xFFFFC107)

/** Provide a Theme color for Success */
@Suppress("UnusedReceiverParameter")
public val Colors.success: Color
  get() = GREEN

/** Provide a Theme color for OnSuccess */
public val Colors.onSuccess: Color
  get() = this.onError

/** Provide a Theme color for Warning */
@Suppress("UnusedReceiverParameter")
public val Colors.warning: Color
  get() = AMBER

/** Provide a Theme color for OnWarning */
public val Colors.onWarning: Color
  get() = this.onError

/** A Spacing extension on the Material theme */
@Suppress("UnusedReceiverParameter")
public val MaterialTheme.keylines: Keylines
  @Composable @ReadOnlyComposable get() = LocalKeylines.current

/**
 * A Material Theme that also knows about Spacing support
 *
 * For additional features you can optionally provider CompositionLocals for:
 *
 * LocalHapticManager - enables PYDroid support for Haptic Feedback
 *
 * LocalActivity - speed up Activity resolution in PYDroid internals
 */
@Composable
public fun PYDroidTheme(
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
