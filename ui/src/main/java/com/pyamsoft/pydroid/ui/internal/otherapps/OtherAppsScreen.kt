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

package com.pyamsoft.pydroid.ui.internal.otherapps

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.ui.internal.app.DialogToolbar
import com.pyamsoft.pydroid.ui.internal.test.createNewTestImageLoader

@Composable
internal fun OtherAppsScreen(
    modifier: Modifier = Modifier,
    state: OtherAppsViewState,
    imageLoader: ImageLoader,
    onNavigationErrorDismissed: () -> Unit,
    onViewStorePage: (index: Int) -> Unit,
    onViewSourceCode: (index: Int) -> Unit,
    onClose: () -> Unit,
) {
  val isLoading = state.isLoading
  val apps = state.apps
  val appsError = state.appsError
  val navigationError = state.navigationError

  val scaffoldState = rememberScaffoldState()

  Scaffold(
      modifier = modifier,
      scaffoldState = scaffoldState,
  ) {
    Column {
      DialogToolbar(
          modifier = Modifier.fillMaxWidth(),
          title = "More pyamsoft apps",
          onClose = onClose,
          imageLoader = imageLoader,
      )
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
private fun OtherAppsList(
    apps: List<OtherApp>,
    imageLoader: ImageLoader,
    onViewStorePage: (index: Int) -> Unit,
    onViewSourceCode: (index: Int) -> Unit,
) {
  Box {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
      itemsIndexed(
          items = apps,
          key = { _, item -> item.packageName },
      ) { index, item ->
        OtherAppsListItem(
            modifier = Modifier.fillMaxWidth(),
            app = item,
            imageLoader = imageLoader,
            onViewSource = { onViewSourceCode(index) },
            onOpenStore = { onViewStorePage(index) },
        )
      }
    }
  }
}

@Composable
private fun ErrorText(error: Throwable) {
  Box(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      contentAlignment = Alignment.Center,
  ) {
    Text(
        text = error.message ?: "An unexpected error has occurred.",
        style = MaterialTheme.typography.body1)
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
private fun PreviewOtherAppsScreen(
    isLoading: Boolean,
    navigationError: Throwable?,
    appsError: Throwable?,
) {
  val context = LocalContext.current

  OtherAppsScreen(
      state =
          OtherAppsViewState(
              isLoading = isLoading,
              apps =
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
                  ),
              navigationError = navigationError,
              appsError = appsError,
          ),
      imageLoader = createNewTestImageLoader(context),
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
