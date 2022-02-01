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

package com.pyamsoft.pydroid.ui.internal.otherapps

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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.ImageLoader
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
import com.pyamsoft.pydroid.ui.internal.app.DialogToolbar
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

@Composable
internal fun OtherAppsScreen(
    modifier: Modifier = Modifier,
    state: OtherAppsViewState,
    imageLoader: ImageLoader,
    onNavigationErrorDismissed: () -> Unit,
    onViewStorePage: (OtherApp) -> Unit,
    onViewSourceCode: (OtherApp) -> Unit,
    onClose: () -> Unit,
) {
  val isLoading = state.isLoading
  val apps = state.apps
  val appsError = state.appsError
  val navigationError = state.navigationError

  val snackbarHostState = remember { SnackbarHostState() }

  Surface(
      modifier = modifier,
      elevation = DialogDefaults.DialogElevation,
  ) {
    Column {
      DialogToolbar(
          modifier = Modifier.fillMaxWidth(),
          title = "More pyamsoft apps",
          onClose = onClose,
      )
      Box(
          contentAlignment = Alignment.BottomCenter,
      ) {
        Crossfade(
            targetState = isLoading,
        ) { loading ->
          if (loading) {
            Loading()
          } else {
            Crossfade(
                targetState = appsError,
            ) { error ->
              if (error != null) {
                ErrorText(
                    error = error,
                )
              } else {
                OtherAppsList(
                    apps = apps,
                    imageLoader = imageLoader,
                    onViewStorePage = onViewStorePage,
                    onViewSourceCode = onViewSourceCode,
                )
              }
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
      modifier = Modifier.fillMaxSize().padding(MaterialTheme.keylines.content),
      contentAlignment = Alignment.Center,
  ) { CircularProgressIndicator() }
}

@Composable
private fun OtherAppsList(
    apps: List<OtherApp>,
    imageLoader: ImageLoader,
    onViewStorePage: (OtherApp) -> Unit,
    onViewSourceCode: (OtherApp) -> Unit,
) {
  LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(MaterialTheme.keylines.baseline),
      contentPadding = PaddingValues(MaterialTheme.keylines.baseline),
  ) {
    items(
        items = apps,
        key = { it.packageName },
    ) { item ->
      OtherAppsListItem(
          modifier = Modifier.fillMaxWidth(),
          app = item,
          imageLoader = imageLoader,
          onViewSource = onViewSourceCode,
          onOpenStore = onViewStorePage,
      )
    }
  }
}

@Composable
private fun ErrorText(error: Throwable) {
  Box(
      modifier = Modifier.fillMaxSize().padding(MaterialTheme.keylines.content),
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = error.message ?: "An unexpected error has occurred.",
        style = MaterialTheme.typography.body1)
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
      onSnackbarDismissed()
    }
  }

  SnackbarHost(
      modifier = modifier,
      hostState = snackbarHost,
  )
}

@Composable
private fun PreviewOtherAppsScreen(
    isLoading: Boolean,
    navigationError: Throwable?,
    appsError: Throwable?,
) {
  OtherAppsScreen(
      state =
          MutableOtherAppsViewState().apply {
            this.isLoading = isLoading
            this.apps =
                listOf(
                    OtherApp(
                        packageName = "test1",
                        name = "Test App 1",
                        description = "Just a test app",
                        icon =
                            "https://raw.githubusercontent.com/pyamsoft/android-project-versions/master/pasterino.png",
                        storeUrl = "some_url",
                        sourceUrl = "some_url",
                    ),
                    OtherApp(
                        packageName = "test2",
                        name = "Test App 2",
                        description = "Just another test app",
                        icon =
                            "https://raw.githubusercontent.com/pyamsoft/android-project-versions/master/pasterino.png",
                        storeUrl = "some_url",
                        sourceUrl = "some_url",
                    ),
                )
            this.navigationError = navigationError
            this.appsError = appsError
          },
      imageLoader = createNewTestImageLoader(),
      onNavigationErrorDismissed = {},
      onViewSourceCode = {},
      onViewStorePage = {},
      onClose = {},
  )
}

@Preview
@Composable
private fun PreviewOtherAppsScreenLoading() {
  PreviewOtherAppsScreen(
      isLoading = true,
      appsError = null,
      navigationError = null,
  )
}

@Preview
@Composable
private fun PreviewOtherAppsScreenLoaded() {
  PreviewOtherAppsScreen(
      isLoading = false,
      appsError = null,
      navigationError = null,
  )
}

@Preview
@Composable
private fun PreviewOtherAppsScreenLoadingWithNavigationError() {
  PreviewOtherAppsScreen(
      isLoading = true,
      appsError = null,
      navigationError = Throwable("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewOtherAppsScreenLoadedWithNavigationError() {
  PreviewOtherAppsScreen(
      isLoading = false,
      appsError = null,
      navigationError = Throwable("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewOtherAppsScreenLoadingWithAppsError() {
  PreviewOtherAppsScreen(
      isLoading = true,
      navigationError = null,
      appsError = Throwable("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewOtherAppsScreenLoadedWithAppsError() {
  PreviewOtherAppsScreen(
      isLoading = false,
      navigationError = null,
      appsError = Throwable("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewOtherAppsScreenLoadingWithBothError() {
  PreviewOtherAppsScreen(
      isLoading = true,
      navigationError = Throwable("TEST ERROR "),
      appsError = Throwable("TEST ERROR"),
  )
}

@Preview
@Composable
private fun PreviewOtherAppsScreenLoadedWithBothError() {
  PreviewOtherAppsScreen(
      isLoading = false,
      navigationError = Throwable("TEST ERROR "),
      appsError = Throwable("TEST ERROR"),
  )
}
