/*
 * Copyright 2026 pyamsoft
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
import coil3.ImageLoader
import com.pyamsoft.pydroid.billing.BillingLauncher
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityDelegateInternal

internal class BillingDialogInjector internal constructor() : ComposableInjector() {

  internal var purchaseClient: BillingLauncher? = null
  internal var viewModel: BillingDialogViewModeler? = null
  internal var imageLoader: ImageLoader? = null

  @CheckResult
  private fun getActivityGraph(activity: ComponentActivity): PYDroidActivityDelegateInternal {
    return ObjectGraph.ActivityScope.retrieve(activity)
  }

  override fun onInject(activity: ComponentActivity) {
    val graph = getActivityGraph(activity)
    graph
        .injector()
        .plusBillingDialog()
        .create(
            provider = graph.changeLogProvider(),
            connected = graph.connectedBilling(),
        )
        .inject(this)
  }

  override fun onDispose() {
    viewModel = null
    imageLoader = null
    purchaseClient = null
  }
}
