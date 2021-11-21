/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.rating

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
@JvmOverloads
internal fun RatingScreen(
    modifier: Modifier = Modifier,
    state: RatingViewState,
    addSnackbarHost: Boolean,
    snackbarHostState: SnackbarHostState,
    onNavigationErrorDismissed: () -> Unit,
) {
  val navigationError = state.navigationError

  if (addSnackbarHost) {
    SnackbarHost(
        modifier = modifier,
        hostState = snackbarHostState,
    )
  }

  NavigationError(
      snackbarHostState = snackbarHostState,
      error = navigationError,
      onSnackbarDismissed = onNavigationErrorDismissed,
  )
}

@Composable
private fun NavigationError(
    snackbarHostState: SnackbarHostState,
    error: Throwable?,
    onSnackbarDismissed: () -> Unit,
) {
  if (error != null) {
    LaunchedEffect(error) {
      snackbarHostState.showSnackbar(
          message = error.message ?: "An unexpected error occurred",
          duration = SnackbarDuration.Long,
      )
      onSnackbarDismissed()
    }
  }
}

@Composable
private fun PreviewRatingScreen(
    navigationError: Throwable?,
) {
  Surface {
    RatingScreen(
        state = MutableRatingViewState().apply { this.navigationError = navigationError },
        addSnackbarHost = true,
        snackbarHostState = SnackbarHostState(),
        onNavigationErrorDismissed = {},
    )
  }
}

@Preview
@Composable
private fun PreviewRatingScreenNoError() {
  PreviewRatingScreen(
      navigationError = null,
  )
}

@Preview
@Composable
private fun PreviewRatingScreenError() {
  PreviewRatingScreen(
      navigationError = RuntimeException("TEST ERROR"),
  )
}
