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
@Suppress("unused")
public val Icons.Outlined.LibraryAdd: ImageVector
  get() {
    if (_libraryAdd != null) {
      return _libraryAdd!!
    }
    _libraryAdd =
        materialIcon(name = "Outlined.LibraryAdd") {
          materialPath {
            moveTo(4.0f, 6.0f)
            lineTo(2.0f, 6.0f)
            verticalLineToRelative(14.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(14.0f)
            verticalLineToRelative(-2.0f)
            lineTo(4.0f, 20.0f)
            lineTo(4.0f, 6.0f)
            close()
            moveTo(20.0f, 2.0f)
            lineTo(8.0f, 2.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(12.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(12.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            lineTo(22.0f, 4.0f)
            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
            close()
            moveTo(20.0f, 16.0f)
            lineTo(8.0f, 16.0f)
            lineTo(8.0f, 4.0f)
            horizontalLineToRelative(12.0f)
            verticalLineToRelative(12.0f)
            close()
            moveTo(13.0f, 14.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-3.0f)
            horizontalLineToRelative(3.0f)
            lineTo(18.0f, 9.0f)
            horizontalLineToRelative(-3.0f)
            lineTo(15.0f, 6.0f)
            horizontalLineToRelative(-2.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(-3.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(3.0f)
            close()
          }
        }
    return _libraryAdd!!
  }

@Suppress("ObjectPropertyName") private var _libraryAdd: ImageVector? = null
