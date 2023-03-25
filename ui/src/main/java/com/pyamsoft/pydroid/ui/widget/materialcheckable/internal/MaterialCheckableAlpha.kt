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

package com.pyamsoft.pydroid.ui.widget.materialcheckable.internal

import androidx.annotation.CheckResult
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.pyamsoft.pydroid.theme.success
import com.pyamsoft.pydroid.ui.widget.materialcheckable.MaterialCheckableAlpha

internal data class MaterialCheckableAlphaImpl
internal constructor(
    override val primary: Float,
    override val secondary: Float,
) : MaterialCheckableAlpha

@Composable
@CheckResult
internal fun rememberMaterialCheckableColor(
    condition: Boolean,
    selectedColor: Color,
): Color {
  val unselectedColor = MaterialTheme.colors.onSurface
  return remember(
      condition,
      unselectedColor,
      selectedColor,
  ) {
    if (condition) selectedColor else unselectedColor
  }
}

@Composable
@CheckResult
internal fun rememberMaterialCheckableIconColor(condition: Boolean): Color {
  return rememberMaterialCheckableColor(
      condition = condition,
      selectedColor = MaterialTheme.colors.success,
  )
}

@Composable
@CheckResult
internal fun rememberMaterialCheckableAlpha(
    isEditable: Boolean,
    condition: Boolean
): MaterialCheckableAlpha {
  val highAlpha = ContentAlpha.high
  val mediumAlpha = ContentAlpha.medium
  val disabledAlpha = ContentAlpha.disabled

  return remember(
      isEditable,
      condition,
      highAlpha,
      mediumAlpha,
      disabledAlpha,
  ) {
    val primary =
        if (isEditable) {
          if (condition) highAlpha else mediumAlpha
        } else disabledAlpha
    val secondary =
        if (isEditable) {
          // High alpha when selected
          if (condition) highAlpha else disabledAlpha
        } else disabledAlpha

    return@remember MaterialCheckableAlphaImpl(primary, secondary)
  }
}
