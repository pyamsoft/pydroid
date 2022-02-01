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

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import com.pyamsoft.pydroid.ui.defaults.SpacingDefaults

public data class Spacing
internal constructor(
    public val keyline: Dp,
    public val padding: Dp,
    public val adjustment: Dp,
)

private val DEFAULT_SPACING =
    Spacing(
        keyline = SpacingDefaults.Keyline,
        padding = SpacingDefaults.Padding,
        adjustment = SpacingDefaults.Adjustment,
    )

/** A Spacing extension on the Material theme */
public val MaterialTheme.spacing: Spacing
  @Composable @ReadOnlyComposable get() = LocalSpacing.current

/** The local spacing construct */
public val LocalSpacing: ProvidableCompositionLocal<Spacing> = staticCompositionLocalOf {
  DEFAULT_SPACING
}
