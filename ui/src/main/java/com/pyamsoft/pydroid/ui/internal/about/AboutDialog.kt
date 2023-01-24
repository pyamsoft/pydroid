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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.util.rememberNotNull

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

  val viewModel = rememberNotNull(component.viewModel)

  val uriHandler = LocalUriHandler.current

  val handleDismissFailedNavigation = { viewModel.handleDismissFailedNavigation() }

  val handleOpenPage = { url: String ->
    handleDismissFailedNavigation()

    try {
      uriHandler.openUri(url)
    } catch (e: Throwable) {
      viewModel.handleFailedNavigation(e)
    }
  }

  MountHooks(
      viewModel = viewModel,
  )

  Dialog(
      properties = rememberDialogProperties(),
      onDismissRequest = onDismiss,
  ) {
    AboutScreen(
        modifier = modifier.padding(MaterialTheme.keylines.content),
        state = viewModel.state,
        onViewHomePage = { handleOpenPage(it.libraryUrl) },
        onViewLicense = { handleOpenPage(it.licenseUrl) },
        onNavigationErrorDismissed = handleDismissFailedNavigation,
        onClose = onDismiss,
    )
  }
}
