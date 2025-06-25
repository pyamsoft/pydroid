/*
 * Copyright 2025 pyamsoft
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

package com.pyamsoft.pydroid.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/** Copied from material-icons-extended since the library itself is too large */
@Suppress("UnusedReceiverParameter")
public val Icons.Outlined.Terminal: ImageVector
  get() {
    if (_terminal != null) {
      return _terminal!!
    }
    _terminal =
        materialIcon(name = "Outlined.Terminal") {
          materialPath {
            moveTo(20.0f, 4.0f)
            horizontalLineTo(4.0f)
            curveTo(2.89f, 4.0f, 2.0f, 4.9f, 2.0f, 6.0f)
            verticalLineToRelative(12.0f)
            curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(16.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            verticalLineTo(6.0f)
            curveTo(22.0f, 4.9f, 21.11f, 4.0f, 20.0f, 4.0f)
            close()
            moveTo(20.0f, 18.0f)
            horizontalLineTo(4.0f)
            verticalLineTo(8.0f)
            horizontalLineToRelative(16.0f)
            verticalLineTo(18.0f)
            close()
            moveTo(18.0f, 17.0f)
            horizontalLineToRelative(-6.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(6.0f)
            verticalLineTo(17.0f)
            close()
            moveTo(7.5f, 17.0f)
            lineToRelative(-1.41f, -1.41f)
            lineTo(8.67f, 13.0f)
            lineToRelative(-2.59f, -2.59f)
            lineTo(7.5f, 9.0f)
            lineToRelative(4.0f, 4.0f)
            lineTo(7.5f, 17.0f)
            close()
          }
        }
    return _terminal!!
  }

@Suppress("ObjectPropertyName") private var _terminal: ImageVector? = null
