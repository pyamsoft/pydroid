/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.billing

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.billing.BillingInteractor
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class BillingViewModeler
internal constructor(
    private val state: MutableBillingViewState,
    private val changeLogInteractor: ChangeLogInteractor,
    private val interactor: BillingInteractor,
    private val provider: AppProvider,
) : AbstractViewModeler<BillingViewState>(state) {

  private val refreshRunner = highlander<Unit> { interactor.refresh() }

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      val displayName = changeLogInteractor.getDisplayName()
      state.apply {
        name = displayName
        icon = provider.applicationIcon
      }
    }

    scope.launch(context = Dispatchers.Main) {
      interactor.watchSkuList { status, list ->
        Logger.d("SKU list updated: $status $list")
        state.apply {
          connected = status
          skuList = list.sortedBy { it.price }
        }
      }
    }

    scope.launch(context = Dispatchers.Main) {
      interactor.watchErrors { error ->
        Logger.e(error, "Billing error received")
        state.error = error
      }
    }
  }

  internal fun handleClearError() {
    state.error = null
  }

  internal fun handleRefresh(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) { refreshRunner.call() }
  }
}
