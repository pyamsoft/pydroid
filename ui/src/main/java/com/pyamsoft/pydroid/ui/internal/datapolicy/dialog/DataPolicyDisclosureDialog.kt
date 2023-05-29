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

package com.pyamsoft.pydroid.ui.internal.datapolicy.dialog

import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import coil.ImageLoader
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.util.rememberActivity
import com.pyamsoft.pydroid.ui.util.rememberNotNull

internal class DataPolicyInjector : ComposableInjector() {

  internal var viewModel: DataPolicyDialogViewModeler? = null
  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getChangelogProvider(activity: FragmentActivity): ChangeLogProvider {
    return ObjectGraph.ActivityScope.retrieve(activity).changeLogProvider()
  }

  override fun onInject(activity: FragmentActivity) {
    ObjectGraph.ApplicationScope.retrieve(activity.application)
        .injector()
        .plusDataPolicyDialog()
        .create(getChangelogProvider(activity))
        .inject(this)
  }

  override fun onDispose() {
    viewModel = null
    imageLoader = null
  }
}

@Composable
private fun MountHooks(
    viewModel: DataPolicyDialogViewModeler,
) {
  LaunchedEffect(
      viewModel,
  ) {
    viewModel.bind(scope = this)
  }
}

@Composable
internal fun DataPolicyDisclosureDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
  val component = rememberComposableInjector { DataPolicyInjector() }

  val activity = rememberActivity()
  val viewModel = rememberNotNull(component.viewModel)
  val imageLoader = rememberNotNull(component.imageLoader)

  val uriHandler = LocalUriHandler.current

  val handleHideNavigationError by rememberUpdatedState { viewModel.handleHideNavigationError() }

  val openPage by rememberUpdatedState { url: String ->
    handleHideNavigationError()

    try {
      uriHandler.openUri(url)
    } catch (e: Throwable) {
      viewModel.handleNavigationFailed(e)
    }
  }

  MountHooks(
      viewModel = viewModel,
  )

  Dialog(
      properties = rememberDialogProperties(),
      onDismissRequest = onDismiss,
  ) {
    DataPolicyDisclosureScreen(
        modifier = modifier.padding(MaterialTheme.keylines.content),
        state = viewModel.state,
        imageLoader = imageLoader,
        onNavigationErrorDismissed = { handleHideNavigationError() },
        onAccept = {
          viewModel.handleAccept(
              onAccepted = onDismiss,
          )
        },
        onReject = {
          viewModel.handleReject(
              onRejected = { activity.finishAndRemoveTask() },
          )
        },
        onPrivacyPolicyClicked = { viewModel.handleViewPrivacyPolicy(openPage) },
        onTermsOfServiceClicked = { viewModel.handleViewTermsOfService(openPage) },
    )
  }
}
