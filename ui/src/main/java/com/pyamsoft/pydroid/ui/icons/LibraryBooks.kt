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

package com.pyamsoft.pydroid.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/** Copied from material-icons-extended since the library itself is too large */
@Suppress("unused")
public val Icons.Outlined.LibraryBooks: ImageVector
  get() {
    if (_libraryBooks != null) {
      return _libraryBooks!!
    }
    _libraryBooks =
        materialIcon(name = "Outlined.LibraryBooks") {
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
            moveTo(10.0f, 9.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(-8.0f)
            close()
            moveTo(10.0f, 12.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(-4.0f)
            close()
            moveTo(10.0f, 6.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(-8.0f)
            close()
          }
        }
    return _libraryBooks!!
  }

@Suppress("ObjectPropertyName") private var _libraryBooks: ImageVector? = null
