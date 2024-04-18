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

package com.pyamsoft.pydroid.ui.defaults

/** Typography defaults */
public object TypographyDefaults {

  /**
   * This is a magic number that is recommended on the M3 migration guide
   *
   * https://developer.android.com/develop/ui/compose/designsystems/material2-material3#m3_16
   *
   * Why this is a magic hardcoded number instead of something defined in the system is beyond me.
   * Are the Google engineers stupid or something?
   */
  public const val ALPHA_DISABLED: Float = 0.38F
}
