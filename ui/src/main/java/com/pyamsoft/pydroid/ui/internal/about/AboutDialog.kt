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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph

internal class AboutDialogInjector : ComposableInjector() {

  internal var viewModel: AboutViewModeler? = null

  override fun onInject(activity: FragmentActivity) {
    ObjectGraph.ApplicationScope.retrieve(activity.application)
        .injector()
        .plusAbout()
        .create()
        .inject(this)
  }

  override fun onDispose() {
    viewModel = null
  }
}

@Composable
private fun MountHooks(
    viewModel: AboutViewModeler,
) {
  LaunchedEffect(
      viewModel,
  ) {
    viewModel.handleLoadLicenses(scope = this)
  }
}

@Composable
internal fun AboutDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
  val component = rememberComposableInjector { AboutDialogInjector() }

  val viewModel = requireNotNull(component.viewModel)

  val uriHandler = LocalUriHandler.current

  val handleDismissFailedNavigation by rememberUpdatedState {
    viewModel.handleDismissFailedNavigation()
  }

  val handleOpenPage by rememberUpdatedState { url: String ->
    handleDismissFailedNavigation()

    try {
      uriHandler.openUri(url)
    } catch (e: Throwable) {
      viewModel.handleFailedNavigation(e)
    }
  }

  val handleViewLicense by rememberUpdatedState { library: OssLibrary ->
    handleOpenPage(library.licenseUrl)
  }

  val handleViewHomePage by rememberUpdatedState { library: OssLibrary ->
    handleOpenPage(library.libraryUrl)
  }

  MountHooks(
      viewModel = viewModel,
  )

  Dialog(
      onDismissRequest = onDismiss,
  ) {
    Box(
        modifier =
            Modifier.fillMaxSize()
                .clickable(
                    // Remove the ripple
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                  onDismiss()
                }
                .padding(MaterialTheme.keylines.content)
                .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
      AboutScreen(
          modifier = modifier,
          state = viewModel.state(),
          onViewHomePage = handleViewHomePage,
          onViewLicense = handleViewLicense,
          onNavigationErrorDismissed = handleDismissFailedNavigation,
          onClose = onDismiss,
      )
    }
  }
}

@Preview
@Composable
private fun PreviewAboutDialog() {
  AboutDialog(
      onDismiss = {},
  )
}
