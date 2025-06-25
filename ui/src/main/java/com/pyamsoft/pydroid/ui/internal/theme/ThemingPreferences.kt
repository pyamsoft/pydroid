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

package com.pyamsoft.pydroid.ui.internal.theme

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.theme.Theming.Mode
import kotlinx.coroutines.flow.Flow

/** Preferences for dark mode */
internal interface ThemingPreferences {

  /** Is application in dark mode */
  @CheckResult fun listenForDarkModeChanges(): Flow<Mode>

  /** Set application dark mode preference */
  fun setDarkMode(mode: Mode)

  /** Is application in material you mode */
  @CheckResult fun listenForMaterialYouChanges(): Flow<Boolean>

  /** Set application material you preference */
  fun setMaterialYou(enabled: Boolean)
}
