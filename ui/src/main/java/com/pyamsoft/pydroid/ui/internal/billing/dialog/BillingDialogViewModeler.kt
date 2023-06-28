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

package com.pyamsoft.pydroid.ui.internal.billing.dialog

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.billing.BillingInteractor
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.ui.app.AppProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class BillingDialogViewModeler
internal constructor(
    state: MutableBillingDialogViewState,
    private val changeLogInteractor: ChangeLogInteractor,
    private val interactor: BillingInteractor,
    private val provider: AppProvider,
) : AbstractViewModeler<BillingDialogViewState>(state) {

  private val vmState = state

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Default) {
      val displayName = changeLogInteractor.getDisplayName()
      vmState.apply {
        name.value = displayName
        icon.value = provider.applicationIcon
      }
    }

    interactor.watchSkuList().also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { snapshot ->
          val status = snapshot.status
          val list = snapshot.skus
          Logger.d("SKU list updated: $status $list")
          vmState.apply {
            connected.value = status
            skuList.value = list.sortedBy { it.price }
          }
        }
      }
    }

    interactor.watchBillingErrors().also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { err ->
          Logger.e(err, "Billing error received")
          vmState.error.value = err
        }
      }
    }
  }

  internal fun handleClearError() {
    vmState.error.value = null
  }

  internal fun handleRefresh(scope: CoroutineScope) {
    if (state.isRefreshing.value) {
      return
    }

    vmState.isRefreshing.value = true
    scope.launch(context = Dispatchers.Default) {
      try {
        interactor.refresh()
      } finally {
        vmState.isRefreshing.value = false
      }
    }
  }
}
