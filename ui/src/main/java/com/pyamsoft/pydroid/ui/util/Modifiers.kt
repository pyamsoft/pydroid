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

package com.pyamsoft.pydroid.ui.util

import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/** Enforces the width is the size of the screen width in portrait mode */
@CheckResult
public fun Modifier.fillUpToPortraitWidth(): Modifier {
  return this.composed {
    val configuration = LocalConfiguration.current

    val width =
        remember(configuration) {
          configuration.run {
            if (isPortrait) {
              // In portrait, we can take up the whole screen width if we want to
              // this will be huge on tablet, but who owns a tablet?
              screenWidthDp
            } else {
              // Else in landscape mode we can use the portrait width * 2 because
              // we have more horizontal estate
              screenHeightDp * 2
            }
          }
        }

    return@composed widthIn(
        max = width.dp,
    )
  }
}

/** Enforces the height is the size of the screen width in portrait mode */
@CheckResult
public fun Modifier.fillUpToPortraitHeight(): Modifier {
  return this.composed {
    val configuration = LocalConfiguration.current

    val height =
        remember(configuration) {
          configuration.run {
            if (isPortrait) {
              // Use the total height in portrait mode
              screenHeightDp
            } else {
              // Use the portrait height in landscape mode, which keeps
              // sizes consistent even during rotation
              screenWidthDp
            }
          }
        }

    return@composed heightIn(
        max = height.dp,
    )
  }
}

/** Enforces that the size of the screen fills up to the portrait dimensions */
@CheckResult
public fun Modifier.fillUpToPortraitSize(): Modifier {
  return this.fillUpToPortraitWidth().fillUpToPortraitHeight()
}
