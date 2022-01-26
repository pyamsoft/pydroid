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

package com.pyamsoft.pydroid.ui.internal.about

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.internal.app.DialogToolbar

@Composable
@OptIn(ExperimentalAnimationApi::class)
internal fun AboutScreen(
    modifier: Modifier = Modifier,
    state: AboutViewState,
    onNavigationErrorDismissed: () -> Unit,
    onViewHomePage: (library: OssLibrary) -> Unit,
    onViewLicense: (library: OssLibrary) -> Unit,
    onClose: () -> Unit,
) {
  val list = state.licenses
  val isLoading = state.isLoading
  val navigationError = state.navigationError
  val scaffoldState = rememberScaffoldState()

  Scaffold(
      backgroundColor = MaterialTheme.colors.background,
      modifier = modifier,
      scaffoldState = scaffoldState,
  ) {
    Column {
      DialogToolbar(
          modifier = Modifier.fillMaxWidth(),
          title = "Open Source Licenses",
          onClose = onClose,
      )
      Crossfade(
          targetState = isLoading,
      ) { loading ->
        if (loading) {
          Loading()
        } else {
          AboutList(
              list = list,
              onViewHomePage = onViewHomePage,
              onViewLicense = onViewLicense,
          )
        }
      }

      NavigationError(
          snackbarHost = scaffoldState.snackbarHostState,
          error = navigationError,
          onSnackbarDismissed = onNavigationErrorDismissed,
      )
    }
  }
}

@Composable
private fun Loading() {
  Box(
      modifier = Modifier.fillMaxHeight().fillMaxWidth().padding(16.dp),
      contentAlignment = Alignment.Center,
  ) { CircularProgressIndicator() }
}

@Composable
private fun AboutList(
    list: List<OssLibrary>,
    onViewHomePage: (library: OssLibrary) -> Unit,
    onViewLicense: (library: OssLibrary) -> Unit,
) {
  Box {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
      items(
          items = list,
          key = { "${it.name}:${it.libraryUrl}" },
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
}

@Composable
private fun NavigationError(
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
private fun PreviewAboutScreen(
    isLoading: Boolean,
    error: Throwable?,
) {

  val state =
      object : AboutViewState {
        override val isLoading: Boolean = isLoading
        override val licenses: List<OssLibrary> = OssLibraries.libraries().sortedBy { it.name }
        override val navigationError: Throwable? = error
      }

  AboutScreen(
      state = state,
      onNavigationErrorDismissed = {},
      onViewLicense = {},
      onViewHomePage = {},
      onClose = {},
  )
}

@Preview
@Composable
private fun PreviewAboutScreenLoading() {
  PreviewAboutScreen(
      isLoading = true,
      error = null,
  )
}

@Preview
@Composable
private fun PreviewAboutScreenLoaded() {
  PreviewAboutScreen(
      isLoading = false,
      error = null,
  )
}

@Preview
@Composable
private fun PreviewAboutScreenLoadingWithError() {
  PreviewAboutScreen(
      isLoading = true,
      error = Throwable("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewAboutScreenLoadedWithError() {
  PreviewAboutScreen(
      isLoading = false,
      error = Throwable("TEST ERROR"),
  )
}
