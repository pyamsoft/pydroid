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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.uri.LocalExternalUriHandler
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.pydroid.util.MarketLinker

internal class ChangeLogDialogInjector : ComposableInjector() {

  internal var viewModel: ChangeLogDialogViewModeler? = null
  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getChangelogProvider(activity: ComponentActivity): ChangeLogProvider {
    return ObjectGraph.ActivityScope.retrieve(activity).changeLogProvider()
  }

  override fun onInject(activity: ComponentActivity) {
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

  val viewModel = rememberNotNull(component.viewModel)
  val imageLoader = rememberNotNull(component.imageLoader)

  val context = LocalContext.current
  val uriHandler = LocalExternalUriHandler.current

  MountHooks(
      viewModel = viewModel,
  )

  Dialog(
      properties = rememberDialogProperties(),
      onDismissRequest = onDismiss,
  ) {
    ChangeLogScreen(
        modifier = modifier.padding(MaterialTheme.keylines.content),
        state = viewModel,
        imageLoader = imageLoader,
        onRateApp = { uriHandler.openUri(MarketLinker.getStorePageLink(context)) },
        onClose = onDismiss,
    )
  }
}
