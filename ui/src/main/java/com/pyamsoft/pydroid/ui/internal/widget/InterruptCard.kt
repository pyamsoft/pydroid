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

package com.pyamsoft.pydroid.ui.internal.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.theme.HairlineSize
import com.pyamsoft.pydroid.ui.defaults.CardDefaults

@Composable
@OptIn(ExperimentalAnimationApi::class)
internal fun InterruptCard(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable () -> Unit,
) {
  AnimatedVisibility(
      visible = visible,
      enter = fadeIn() + scaleIn(),
      exit = scaleOut() + fadeOut(),
  ) {
    Surface(
        modifier = modifier,
        border =
            BorderStroke(
                width = HairlineSize,
                color = MaterialTheme.colors.primary,
            ),
        elevation = CardDefaults.Elevation,
        color =
            MaterialTheme.colors.primary.copy(
                alpha = 0.10F,
            ),
        shape = MaterialTheme.shapes.medium,
    ) {
      content()
    }
  }
}
