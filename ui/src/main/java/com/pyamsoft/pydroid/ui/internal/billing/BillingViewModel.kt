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

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.billing.BillingInteractor
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.core.Logger



import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class BillingViewModel
internal constructor(
    private val changeLogInteractor: ChangeLogInteractor,
    private val interactor: BillingInteractor,
    provider: AppProvider,
) :
    UiViewModel<BillingViewState, BillingControllerEvent>(
        initialState =
            BillingViewState(
                skuList = emptyList(),
                connected = BillingState.LOADING,
                error = null,
                icon = 0,
                name = "",
            )) {

  init {
    viewModelScope.launch(context = Dispatchers.Default) {
      val displayName = changeLogInteractor.getDisplayName()
      setState {
        copy(
            name = displayName,
            icon = provider.applicationIcon,
        )
      }
    }

    viewModelScope.launch(context = Dispatchers.Default) {
      interactor.watchSkuList { connected, list ->
        Logger.d("SKU list updated: $connected $list")
        setState {
          copy(
              connected = connected,
              skuList = list.sortedBy { it.price },
          )
        }
      }
    }

    viewModelScope.launch(context = Dispatchers.Default) {
      interactor.watchErrors { error ->
        Logger.e(error, "Billing error received")
        setState { copy(error = error) }
      }
    }
  }

  internal fun handleClearError() {
    setState { copy(error = null) }
  }

  internal fun handleRefresh() {
    viewModelScope.launch(context = Dispatchers.Default) { interactor.refresh() }
  }

  internal fun handlePurchase(
      index: Int,
  ) {
    val skuList = state.skuList
    if (skuList.isEmpty() || skuList.size <= index) {
      Logger.w("SKU index out of bounds: $index ${skuList.size}")
      setState { copy(error = IllegalStateException("Unable to purchase in-app item")) }
      return
    }

    setState(
        { copy(error = null) },
        andThen = {
          val sku = skuList[index]
          publish(BillingControllerEvent.Purchase(sku))
        })
  }
}
