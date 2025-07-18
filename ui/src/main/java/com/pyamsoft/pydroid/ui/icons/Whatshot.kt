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
public val Icons.Outlined.Whatshot: ImageVector
  get() {
    if (_whatshot != null) {
      return _whatshot!!
    }
    _whatshot =
        materialIcon(name = "Outlined.Whatshot") {
          materialPath {
            moveTo(11.57f, 13.16f)
            curveToRelative(-1.36f, 0.28f, -2.17f, 1.16f, -2.17f, 2.41f)
            curveToRelative(0.0f, 1.34f, 1.11f, 2.42f, 2.49f, 2.42f)
            curveToRelative(2.05f, 0.0f, 3.71f, -1.66f, 3.71f, -3.71f)
            curveToRelative(0.0f, -1.07f, -0.15f, -2.12f, -0.46f, -3.12f)
            curveToRelative(-0.79f, 1.07f, -2.2f, 1.72f, -3.57f, 2.0f)
            close()
            moveTo(13.5f, 0.67f)
            reflectiveCurveToRelative(0.74f, 2.65f, 0.74f, 4.8f)
            curveToRelative(0.0f, 2.06f, -1.35f, 3.73f, -3.41f, 3.73f)
            curveToRelative(-2.07f, 0.0f, -3.63f, -1.67f, -3.63f, -3.73f)
            lineToRelative(0.03f, -0.36f)
            curveTo(5.21f, 7.51f, 4.0f, 10.62f, 4.0f, 14.0f)
            curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
            reflectiveCurveToRelative(8.0f, -3.58f, 8.0f, -8.0f)
            curveTo(20.0f, 8.61f, 17.41f, 3.8f, 13.5f, 0.67f)
            close()
            moveTo(12.0f, 20.0f)
            curveToRelative(-3.31f, 0.0f, -6.0f, -2.69f, -6.0f, -6.0f)
            curveToRelative(0.0f, -1.53f, 0.3f, -3.04f, 0.86f, -4.43f)
            curveToRelative(1.01f, 1.01f, 2.41f, 1.63f, 3.97f, 1.63f)
            curveToRelative(2.66f, 0.0f, 4.75f, -1.83f, 5.28f, -4.43f)
            curveTo(17.34f, 8.97f, 18.0f, 11.44f, 18.0f, 14.0f)
            curveToRelative(0.0f, 3.31f, -2.69f, 6.0f, -6.0f, 6.0f)
            close()
          }
        }
    return _whatshot!!
  }

@Suppress("ObjectPropertyName") private var _whatshot: ImageVector? = null
