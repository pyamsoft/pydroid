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

package com.pyamsoft.pydroid.ui.internal.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Transition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.ui.theme.HairlineSize
import com.pyamsoft.pydroid.ui.theme.ZeroElevation
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun WatchTransition(
    transition: Transition<EnterExitState>,
    onShown: () -> Unit,
    onHidden: () -> Unit,
) {
  val handleShow by rememberUpdatedState(onShown)
  val handleHide by rememberUpdatedState(onHidden)

  LaunchedEffect(transition) {
    snapshotFlow { transition.currentState }
        .distinctUntilChanged()
        .collect { state ->
          when (state) {
            EnterExitState.PreEnter -> {
              // Do nothing before enter
            }
            EnterExitState.Visible -> {
              handleShow()
            }
            EnterExitState.PostExit -> {
              handleHide()
            }
          }
        }
  }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
internal fun InterruptCard(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onShown: () -> Unit = {},
    onHidden: () -> Unit = {},
    content: @Composable () -> Unit,
) {
  AnimatedVisibility(
      visible = visible,
      enter = fadeIn() + scaleIn(),
      exit = scaleOut() + fadeOut(),
  ) {
    val self = this

    // Watch the status of the transition animation
    WatchTransition(
        transition = self.transition,
        onShown = onShown,
        onHidden = onHidden,
    )

    Surface(
        modifier = modifier,
        border =
            BorderStroke(
                width = HairlineSize,
                color = MaterialTheme.colors.primary,
            ),
        elevation = ZeroElevation,
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
