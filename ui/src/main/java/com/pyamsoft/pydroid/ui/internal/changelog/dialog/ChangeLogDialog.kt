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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import coil.ImageLoader
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.app.invoke
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.util.rememberActivity
import com.pyamsoft.pydroid.util.MarketLinker

internal class ChangeLogDialogInjector() : ComposableInjector() {

  internal var viewModel: ChangeLogDialogViewModeler? = null
  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getChangelogProvider(activity: FragmentActivity): ChangeLogProvider {
    return ObjectGraph.ActivityScope.retrieve(activity).changeLogProvider()
  }

  override fun onInject(activity: FragmentActivity) {
    ObjectGraph.ActivityScope.retrieve(activity)
        .injector()
        .plusChangeLogDialog()
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
    viewModel: ChangeLogDialogViewModeler,
) {
  LaunchedEffect(
      viewModel,
  ) {
    viewModel.bind(scope = this)
  }
}

@Composable
internal fun ChangeLogDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
  val component = rememberComposableInjector { ChangeLogDialogInjector() }

  val activity = rememberActivity()
  val viewModel = requireNotNull(component.viewModel)
  val imageLoader = requireNotNull(component.imageLoader)

  val uriHandler = LocalUriHandler.current

  val handleRateApp by rememberUpdatedState {
    uriHandler.openUri(MarketLinker.getStorePageLink(activity))
  }

  MountHooks(
      viewModel = viewModel,
  )

  Dialog(
      onDismissRequest = onDismiss,
  ) {
    Box(
        modifier =
            Modifier.fillMaxSize().padding(MaterialTheme.keylines.content).systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
      ChangeLogScreen(
          modifier = modifier.fillMaxWidth(),
          state = viewModel.state(),
          imageLoader = imageLoader,
          onRateApp = handleRateApp,
          onClose = onDismiss,
      )
    }
  }
}

@Preview
@Composable
private fun PreviewChangeLogDialog() {
  ChangeLogDialog(
      onDismiss = {},
  )
}
