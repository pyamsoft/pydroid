/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.theme

import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDelegate
import com.pyamsoft.pydroid.ui.theme.Theming.Mode
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.DARK
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.LIGHT
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.SYSTEM

@CheckResult
internal fun String.toThemingMode(): Mode {
  return when (this) {
    "light" -> LIGHT
    "dark" -> DARK
    "system" -> SYSTEM
    else -> SYSTEM
  }
}

@CheckResult
internal fun Mode.toRawString(): String =
    when (this) {
      LIGHT -> "light"
      DARK -> "dark"
      SYSTEM -> "system"
    }

@CheckResult
internal fun Mode.toAppCompatMode(): Int =
    when (this) {
      LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
      DARK -> AppCompatDelegate.MODE_NIGHT_YES
      else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
