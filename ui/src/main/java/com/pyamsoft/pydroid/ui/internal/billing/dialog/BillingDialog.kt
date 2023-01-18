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

package com.pyamsoft.pydroid.ui.internal.billing.dialog

import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import com.pyamsoft.pydroid.billing.BillingLauncher
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.app.AppProvider
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.util.LifecycleEffect
import com.pyamsoft.pydroid.ui.util.rememberActivity
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class BillingDialogInjector : ComposableInjector() {

  internal var purchaseClient: BillingLauncher? = null
  internal var viewModel: BillingDialogViewModeler? = null
  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getApplicationProvider(activity: FragmentActivity): AppProvider {
    return ObjectGraph.ActivityScope.retrieve(activity).changeLogProvider()
  }

  override fun onInject(activity: FragmentActivity) {
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

  LifecycleEffect {
    object : DefaultLifecycleObserver {

      override fun onResume(owner: LifecycleOwner) {
        viewModel.handleRefresh(owner.lifecycleScope)
      }
    }
  }
}

@Composable
internal fun BillingDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
  val component = rememberComposableInjector { BillingDialogInjector() }

  val activity = rememberActivity()
  val viewModel = rememberNotNull(component.viewModel)
  val imageLoader = rememberNotNull(component.imageLoader)
  val purchaseClient = rememberNotNull(component.purchaseClient)

  val handleLaunchPurchase by rememberUpdatedState { sku: BillingSku ->
    // Enforce on main thread since billing is Google
    activity.lifecycleScope.launch(context = Dispatchers.Main) {
      Enforcer.assertOnMainThread()

      Logger.d("Start purchase flow for $sku")
      purchaseClient.requireNotNull().purchase(activity, sku)
    }

    return@rememberUpdatedState
  }

  MountHooks(
      viewModel = viewModel,
  )

  Dialog(
      onDismissRequest = onDismiss,
  ) {
    BillingScreen(
        modifier = modifier.padding(MaterialTheme.keylines.content),
        state = viewModel.state,
        imageLoader = imageLoader,
        onPurchase = handleLaunchPurchase,
        onBillingErrorDismissed = { viewModel.handleClearError() },
        onClose = onDismiss,
    )
  }
}
