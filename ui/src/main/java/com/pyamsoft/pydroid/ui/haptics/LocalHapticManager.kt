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

package com.pyamsoft.pydroid.ui.haptics

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/**
 * The HapticManager instance tied to the LocalView
 *
 * We use a composositionLocal because even though our backing LocalView is static, I have no idea
 * how likely rememberCoroutineScope() is to recompose, so we play it safe.
 */
@JvmField
public val LocalHapticManager: ProvidableCompositionLocal<HapticManager?> = compositionLocalOf {
  null
}
