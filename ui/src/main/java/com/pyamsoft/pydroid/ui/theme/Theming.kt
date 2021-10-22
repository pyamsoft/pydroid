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

import android.app.Activity
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDelegate

/** Handles getting current dark mode state and setting dark mode state */
public interface Theming {

  /** Initialize the Theming module */
  public suspend fun init()

  /** Is activity dark mode */
  @CheckResult public fun isDarkTheme(activity: Activity): Boolean

  /** Get current mode */
  @CheckResult public suspend fun getMode(): Mode

  /** Set application wide dark mode */
  public suspend fun setDarkTheme(mode: Mode)

  /** Dark mode enum */
  public enum class Mode {
    /** Light mode */
    LIGHT,

    /** Dark mode */
    DARK,

    /** System mode */
    SYSTEM;
  }
}
