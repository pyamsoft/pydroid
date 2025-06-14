/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.internal.billing.dialog

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil3.ImageLoader
import com.pyamsoft.pydroid.billing.BillingLauncher
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.AppProvider
import com.pyamsoft.pydroid.ui.app.rememberDialogProperties
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.util.rememberResolvedActivity
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class BillingDialogInjector : ComposableInjector() {

  internal var purchaseClient: BillingLauncher? = null
  internal var viewModel: BillingDialogViewModeler? = null
  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getApplicationProvider(activity: ComponentActivity): AppProvider {
    return ObjectGraph.ActivityScope.retrieve(activity).changeLogProvider()
  }

  override fun onInject(activity: ComponentActivity) {
    ObjectGraph.ActivityScope.retrieve(activity)
        .injector()
        .plusBillingDialog()
        .create(getApplicationProvider(activity))
        .inject(this)
  }

  override fun onDispose() {
    viewModel = null
    imageLoader = null
    purchaseClient = null
  }
}

@Composable
private fun MountHooks(
    viewModel: BillingDialogViewModeler,
) {
  LaunchedEffect(
      viewModel,
  ) {
    viewModel.bind(scope = this)
  }

  // Use the LifecycleOwner.CoroutineScope (Activity usually)
  // so that the scope does not die because of navigation events
  val owner = LocalLifecycleOwner.current
  val lifecycleScope = owner.lifecycleScope

  LifecycleEventEffect(
      event = Lifecycle.Event.ON_RESUME,
  ) {
    viewModel.handleRefresh(scope = lifecycleScope)
  }
}

@Composable
internal fun BillingDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
  val component = rememberComposableInjector { BillingDialogInjector() }
  val viewModel = rememberNotNull(component.viewModel)
  val imageLoader = rememberNotNull(component.imageLoader)
  val purchaseClient = rememberNotNull(component.purchaseClient)

  // Required to launch Billing flows
  val activity = rememberResolvedActivity()

  MountHooks(
      viewModel = viewModel,
  )

  Dialog(
      properties = rememberDialogProperties(),
      onDismissRequest = onDismiss,
  ) {
    BillingScreen(
        modifier = modifier.padding(MaterialTheme.keylines.content),
        state = viewModel,
        imageLoader = imageLoader,
        onPurchase = { sku ->
          // Enforce on main thread since billing is Google
          // Use the Activity scope so that we are not randomly cancelled mid purchase
          activity.lifecycleScope.launch(context = Dispatchers.Main) {
            Logger.d { "Start purchase flow for $sku" }
            purchaseClient.purchase(activity, sku)
          }
        },
        onBillingErrorDismissed = { viewModel.handleClearError() },
        onClose = onDismiss,
    )
  }
}
