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

package com.pyamsoft.pydroid.ui.haptics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import com.pyamsoft.pydroid.ui.internal.haptics.AndroidViewHapticManager
import com.pyamsoft.pydroid.ui.internal.haptics.NoopHapticManager

/**
 * Right now compose only has support for a few [HapticFeedbackType], which are too limited.
 *
 * We can, however, shim the haptic support by pulling from the existing View layer of haptics. We
 * do this here, and avoid the old Int based interface via the manager functions
 */
@Composable
public fun rememberHapticManager(): HapticManager {
  // Is in edit mode
  if (LocalInspectionMode.current) {
    return remember { NoopHapticManager }
  }

  val view = LocalView.current
  return remember(view) { AndroidViewHapticManager(view) }
}
