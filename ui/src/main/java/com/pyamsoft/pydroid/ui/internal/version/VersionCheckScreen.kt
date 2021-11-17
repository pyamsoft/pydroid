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

package com.pyamsoft.pydroid.ui.internal.version

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
internal fun VersionCheckScreen(
    modifier: Modifier = Modifier,
    state: VersionCheckViewState,
    addSnackbarHost: Boolean,
    snackbarHostState: SnackbarHostState,
    onNavigationErrorDismissed: () -> Unit,
    onVersionCheckErrorDismissed: () -> Unit,
) {
  val isLoading = state.isLoading
  val versionCheckError = state.versionCheckError
  val navigationError = state.navigationError

  if (addSnackbarHost) {
    SnackbarHost(
        modifier = modifier,
        hostState = snackbarHostState,
    )
  }

  Loading(
      snackbarHost = snackbarHostState,
      isLoading = isLoading,
  )

  VersionCheckError(
      snackbarHost = snackbarHostState,
      error = versionCheckError,
      onSnackbarDismissed = onVersionCheckErrorDismissed,
  )

  NavigationError(
      snackbarHost = snackbarHostState,
      error = navigationError,
      onSnackbarDismissed = onNavigationErrorDismissed,
  )
}

@Composable
private fun Loading(
    snackbarHost: SnackbarHostState,
    isLoading: Boolean,
) {
  if (isLoading) {
    LaunchedEffect(isLoading) {
      snackbarHost.showSnackbar(
          message = "Checking for updates",
          duration = SnackbarDuration.Indefinite,
      )
    }
  }
}

@Composable
private fun VersionCheckError(
    snackbarHost: SnackbarHostState,
    error: Throwable?,
    onSnackbarDismissed: () -> Unit,
) {
  SnackbarError(
      snackbarHost = snackbarHost,
      error = error,
      onSnackbarDismissed = onSnackbarDismissed,
  )
}

@Composable
private fun NavigationError(
    snackbarHost: SnackbarHostState,
    error: Throwable?,
    onSnackbarDismissed: () -> Unit,
) {
  SnackbarError(
      snackbarHost = snackbarHost,
      error = error,
      onSnackbarDismissed = onSnackbarDismissed,
  )
}

@Composable
private fun SnackbarError(
    snackbarHost: SnackbarHostState,
    error: Throwable?,
    onSnackbarDismissed: () -> Unit,
) {
  if (error != null) {
    LaunchedEffect(error) {
      snackbarHost.showSnackbar(
          message = error.message ?: "An unexpected error occurred",
          duration = SnackbarDuration.Long,
      )
      onSnackbarDismissed()
    }
  }
}

@Composable
private fun PreviewVersionCheckScreen(
    isLoading: Boolean,
    versionCheckError: Throwable?,
    navigationError: Throwable?,
) {
  Surface {
    VersionCheckScreen(
        state =
            VersionCheckViewState(
                isLoading = isLoading,
                versionCheckError = versionCheckError,
                navigationError = navigationError,
            ),
        addSnackbarHost = true,
        snackbarHostState = SnackbarHostState(),
        onVersionCheckErrorDismissed = {},
        onNavigationErrorDismissed = {},
    )
  }
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotLoadingNoErrors() {
  PreviewVersionCheckScreen(
      isLoading = false,
      versionCheckError = null,
      navigationError = null,
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotLoadingVersionCheckError() {
  PreviewVersionCheckScreen(
      isLoading = false,
      versionCheckError = RuntimeException("TEST ERROR"),
      navigationError = null,
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotLoadingNavigationError() {
  PreviewVersionCheckScreen(
      isLoading = false,
      versionCheckError = null,
      navigationError = RuntimeException("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenNotLoadingAllErrors() {
  PreviewVersionCheckScreen(
      isLoading = false,
      versionCheckError = RuntimeException("TEST ERROR"),
      navigationError = RuntimeException("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenLoadingNoErrors() {
  PreviewVersionCheckScreen(
      isLoading = true,
      versionCheckError = null,
      navigationError = null,
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenLoadingVersionCheckError() {
  PreviewVersionCheckScreen(
      isLoading = true,
      versionCheckError = RuntimeException("TEST ERROR"),
      navigationError = null,
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenLoadingNavigationError() {
  PreviewVersionCheckScreen(
      isLoading = true,
      versionCheckError = null,
      navigationError = RuntimeException("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewVersionCheckScreenLoadingAllErrors() {
  PreviewVersionCheckScreen(
      isLoading = true,
      versionCheckError = RuntimeException("TEST ERROR"),
      navigationError = RuntimeException("TEST ERROR"),
  )
}
