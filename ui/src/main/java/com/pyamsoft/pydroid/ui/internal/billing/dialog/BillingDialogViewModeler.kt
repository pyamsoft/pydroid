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

import com.pyamsoft.highlander.highlander
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
    override val state: MutableBillingDialogViewState,
    private val changeLogInteractor: ChangeLogInteractor,
    private val interactor: BillingInteractor,
    private val provider: AppProvider,
) : AbstractViewModeler<BillingDialogViewState>(state) {

  private val refreshRunner =
      highlander<Unit>(
          context = Dispatchers.IO,
      ) {
        interactor.refresh()
      }

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      val displayName = changeLogInteractor.getDisplayName()
      state.apply {
        name.value = displayName
        icon.value = provider.applicationIcon
      }
    }

    scope.launch(context = Dispatchers.Main) {
      interactor.watchSkuList { status, list ->
        Logger.d("SKU list updated: $status $list")
        state.apply {
          connected.value = status
          skuList.value = list.sortedBy { it.price }
        }
      }
    }

    scope.launch(context = Dispatchers.Main) {
      interactor.watchErrors { error ->
        Logger.e(error, "Billing error received")
        state.error.value = error
      }
    }
  }

  internal fun handleClearError() {
    state.error.value = null
  }

  internal fun handleRefresh(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) { refreshRunner.call() }
  }
}
