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

package com.pyamsoft.pydroid.ui.defaults

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Default values for Dialogs */
public object DialogDefaults {

  /**
   * Elevation for a Dialog
   *
   * NOTE: This elevation value does not match the MD spec because the value of the elevation is
   * used in Dark mode to brighten the color of a Surface.
   *
   * Because the Surface would get overly bright with 24.dp or 16.dp, we set the elevation to this
   * low value.
   *
   * This has the unfortunate side effect of making the shadow on the surface only slightly elevated
   * as well though.
   */
  public val Elevation: Dp = 1.dp
}
