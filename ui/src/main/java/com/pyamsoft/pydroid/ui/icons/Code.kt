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

package com.pyamsoft.pydroid.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/** Copied from material-icons-extended since the library itself is too large */
@Suppress("UnusedReceiverParameter")
public val Icons.Outlined.Code: ImageVector
  get() {
    if (_code != null) {
      return _code!!
    }
    _code =
        materialIcon(name = "Outlined.Code") {
          materialPath {
            moveTo(9.4f, 16.6f)
            lineTo(4.8f, 12.0f)
            lineToRelative(4.6f, -4.6f)
            lineTo(8.0f, 6.0f)
            lineToRelative(-6.0f, 6.0f)
            lineToRelative(6.0f, 6.0f)
            lineToRelative(1.4f, -1.4f)
            close()
            moveTo(14.6f, 16.6f)
            lineToRelative(4.6f, -4.6f)
            lineToRelative(-4.6f, -4.6f)
            lineTo(16.0f, 6.0f)
            lineToRelative(6.0f, 6.0f)
            lineToRelative(-6.0f, 6.0f)
            lineToRelative(-1.4f, -1.4f)
            close()
          }
        }
    return _code!!
  }

@Suppress("ObjectPropertyName") private var _code: ImageVector? = null
