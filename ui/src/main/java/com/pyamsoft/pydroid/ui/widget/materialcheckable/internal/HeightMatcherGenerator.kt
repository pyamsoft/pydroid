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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.ui.widget.materialcheckable.HeightMatcher
import com.pyamsoft.pydroid.ui.widget.materialcheckable.HeightMatcherGenerator

internal class HeightMatcherGeneratorImpl<T : Any>
internal constructor(
    private val gapHeightGenerator: @Composable (T) -> Dp,
    private val onSizeChangedModifierGenerator: (T) -> Modifier,
) : HeightMatcherGenerator<T> {

  @Composable
  override fun generateFor(item: T): HeightMatcher {
    return HeightMatcherImpl(
        extraHeight = gapHeightGenerator(item),
        onSizeChangedModifier = onSizeChangedModifierGenerator(item),
    )
  }
}

@CheckResult
internal fun <T : Any> createGapHeightGenerator(
    density: Density,
    largest: Int,
    heights: Map<T, Int>
): @Composable (T) -> Dp {
  return { item ->
    remember(
        largest,
        density,
        heights,
        item,
    ) {
      val thisHeight: Int = heights[item] ?: return@remember ZeroSize

      val diff = largest - thisHeight
      if (diff < 0) {
        return@remember ZeroSize
      }

      return@remember density.run { diff.toDp() }
    }
  }
}

@CheckResult
internal fun <T : Any> createOnSizeChangedModifierGenerator(
    heights: Map<T, Int>,
    setHeights: (Map<T, Int>) -> Unit,
): (T) -> Modifier {
  return { item ->
    Modifier.onSizeChanged { size ->
      // Only do this once, on the initial measure
      val height = size.height
      val entry: Int? = heights[item]

      if (entry == null) {
        setHeights(
            heights.toMutableMap().apply {
              this.set(
                  key = item,
                  value = height,
              )
            },
        )
      }
    }
  }
}
