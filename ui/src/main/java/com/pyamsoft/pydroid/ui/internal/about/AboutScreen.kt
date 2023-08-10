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

package com.pyamsoft.pydroid.ui.internal.about

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
import com.pyamsoft.pydroid.ui.internal.app.DialogToolbar
import com.pyamsoft.pydroid.ui.util.collectAsStateList

private enum class AboutScreenContentTypes {
  LIBRARY
}

@Composable
internal fun AboutScreen(
    modifier: Modifier = Modifier,
    state: AboutViewState,
    onNavigationErrorDismissed: () -> Unit,
    onViewHomePage: (library: OssLibrary) -> Unit,
    onViewLicense: (library: OssLibrary) -> Unit,
    onClose: () -> Unit,
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val loadingState by state.loadingState.collectAsState()
  val navigationError by state.navigationError.collectAsState()

  Column(
      modifier = modifier,
  ) {
    DialogToolbar(
        modifier = Modifier.fillMaxWidth(),
        title = "Open Source Licenses",
        onClose = onClose,
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        elevation = DialogDefaults.Elevation,
        shape =
            MaterialTheme.shapes.medium.copy(
                topEnd = ZeroCornerSize,
                topStart = ZeroCornerSize,
            ),
    ) {
      Column(
          modifier = Modifier.fillMaxWidth(),
      ) {
        Crossfade(
            label = "Open Source Licenses",
            targetState = loadingState,
        ) { loading ->
          when (loading) {
            AboutViewState.LoadingState.NONE,
            AboutViewState.LoadingState.LOADING -> {
              Loading()
            }
            AboutViewState.LoadingState.DONE -> {
              AboutList(
                  modifier = Modifier.fillMaxSize(),
                  state = state,
                  onViewHomePage = onViewHomePage,
                  onViewLicense = onViewLicense,
              )
            }
          }
        }

        NavigationError(
            snackbarHost = snackbarHostState,
            error = navigationError,
            onSnackbarDismissed = onNavigationErrorDismissed,
        )
      }
    }
  }
}

@Composable
private fun Loading() {
  Box(
      modifier = Modifier.fillMaxSize().padding(all = MaterialTheme.keylines.content),
      contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}

@Composable
private fun AboutList(
    modifier: Modifier = Modifier,
    state: AboutViewState,
    onViewHomePage: (library: OssLibrary) -> Unit,
    onViewLicense: (library: OssLibrary) -> Unit,
) {
  val licenses = state.licenses.collectAsStateList()

  LazyColumn(
      modifier = modifier,
      verticalArrangement = Arrangement.spacedBy(MaterialTheme.keylines.content),
      contentPadding = PaddingValues(MaterialTheme.keylines.baseline),
  ) {
    items(
        items = licenses,
        key = { "${it.name}:${it.libraryUrl}" },
        contentType = { AboutScreenContentTypes.LIBRARY },
    ) { item ->
      AboutListItem(
          modifier = Modifier.fillMaxWidth(),
          library = item,
          onViewHomePage = { onViewHomePage(item) },
          onViewLicense = { onViewLicense(item) },
      )
    }
  }
}

@Composable
private fun NavigationError(
    modifier: Modifier = Modifier,
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

      // We ignore the showSnackbar result because we don't care (no actions)
      onSnackbarDismissed()
    }
  }

  SnackbarHost(
      modifier = modifier,
      hostState = snackbarHost,
  )
}

@Composable
private fun PreviewAboutScreen(
    loading: AboutViewState.LoadingState,
    error: Throwable?,
) {

  AboutScreen(
      state =
          MutableAboutViewState().apply {
            loadingState.value = loading
            licenses.value = OssLibraries.libraries().sortedBy { it.name }
            navigationError.value = error
          },
      onNavigationErrorDismissed = {},
      onViewLicense = {},
      onViewHomePage = {},
      onClose = {},
  )
}

@Preview
@Composable
private fun PreviewAboutScreenDefault() {
  PreviewAboutScreen(
      loading = AboutViewState.LoadingState.NONE,
      error = null,
  )
}

@Preview
@Composable
private fun PreviewAboutScreenLoading() {
  PreviewAboutScreen(
      loading = AboutViewState.LoadingState.LOADING,
      error = null,
  )
}

@Preview
@Composable
private fun PreviewAboutScreenLoaded() {
  PreviewAboutScreen(
      loading = AboutViewState.LoadingState.DONE,
      error = null,
  )
}

@Preview
@Composable
private fun PreviewAboutScreenDefaultWithError() {
  PreviewAboutScreen(
      loading = AboutViewState.LoadingState.NONE,
      error = RuntimeException("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewAboutScreenLoadingWithError() {
  PreviewAboutScreen(
      loading = AboutViewState.LoadingState.LOADING,
      error = RuntimeException("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewAboutScreenLoadedWithError() {
  PreviewAboutScreen(
      loading = AboutViewState.LoadingState.DONE,
      error = RuntimeException("TEST ERROR"),
  )
}
