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

package com.pyamsoft.pydroid.ui.theme

import android.app.Activity
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.theme.Theming.Mode
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.DARK
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.LIGHT
import com.pyamsoft.pydroid.ui.theme.Theming.Mode.SYSTEM
import kotlinx.coroutines.flow.Flow

/** Handles getting current dark mode state and setting dark mode state */
public interface Theming {

  /** Initialize the Theming module */
  public suspend fun init()

  /** Is activity dark mode */
  @CheckResult public fun isDarkTheme(activity: Activity): Boolean

  /** Get current mode */
  @CheckResult public suspend fun listenForModeChanges(): Flow<Mode>

  /** Set application wide dark mode */
  public suspend fun setDarkTheme(mode: Mode)

  /** Dark mode enum */
  public enum class Mode {
    /** Light mode */
    LIGHT,

    /** Dark mode */
    DARK,

    /** System mode */
    SYSTEM
  }
}

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
