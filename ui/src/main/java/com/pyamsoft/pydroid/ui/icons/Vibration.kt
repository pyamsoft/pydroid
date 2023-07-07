/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/** Copied from material-icons-extended since the library itself is too large */
@Suppress("UnusedReceiverParameter")
public val Icons.Outlined.Vibration: ImageVector
  get() {
    if (_vibration != null) {
      return _vibration!!
    }
    _vibration =
        materialIcon(name = "Outlined.Vibration") {
          materialPath {
            moveTo(0.0f, 15.0f)
            horizontalLineToRelative(2.0f)
            lineTo(2.0f, 9.0f)
            lineTo(0.0f, 9.0f)
            verticalLineToRelative(6.0f)
            close()
            moveTo(3.0f, 17.0f)
            horizontalLineToRelative(2.0f)
            lineTo(5.0f, 7.0f)
            lineTo(3.0f, 7.0f)
            verticalLineToRelative(10.0f)
            close()
            moveTo(22.0f, 9.0f)
            verticalLineToRelative(6.0f)
            horizontalLineToRelative(2.0f)
            lineTo(24.0f, 9.0f)
            horizontalLineToRelative(-2.0f)
            close()
            moveTo(19.0f, 17.0f)
            horizontalLineToRelative(2.0f)
            lineTo(21.0f, 7.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(10.0f)
            close()
            moveTo(16.5f, 3.0f)
            horizontalLineToRelative(-9.0f)
            curveTo(6.67f, 3.0f, 6.0f, 3.67f, 6.0f, 4.5f)
            verticalLineToRelative(15.0f)
            curveToRelative(0.0f, 0.83f, 0.67f, 1.5f, 1.5f, 1.5f)
            horizontalLineToRelative(9.0f)
            curveToRelative(0.83f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
            verticalLineToRelative(-15.0f)
            curveToRelative(0.0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
            close()
            moveTo(16.0f, 19.0f)
            lineTo(8.0f, 19.0f)
            lineTo(8.0f, 5.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(14.0f)
            close()
          }
        }
    return _vibration!!
  }

@Suppress("ObjectPropertyName") private var _vibration: ImageVector? = null
