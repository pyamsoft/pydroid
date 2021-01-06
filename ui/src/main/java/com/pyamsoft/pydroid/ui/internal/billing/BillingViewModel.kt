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
import com.pyamsoft.pydroid.billing.BillingError
import com.pyamsoft.pydroid.billing.BillingInteractor
import com.pyamsoft.pydroid.billing.BillingPurchaseListener
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

internal class BillingViewModel internal constructor(
    private val changeLogInteractor: ChangeLogInteractor,
    private val listener: BillingPurchaseListener,
    private val interactor: BillingInteractor,
    provider: AppProvider,
) : UiViewModel<BillingDialogViewState, BillingDialogViewEvent, BillingDialogControllerEvent>(
    initialState = BillingDialogViewState(
        skuList = emptyList(),
        connected = BillingState.LOADING,
        error = null,
        icon = 0,
        name = "",
    )
) {

    init {
        viewModelScope.launch(context = Dispatchers.Default) {
            val displayName = changeLogInteractor.getDisplayName(provider.applicationPackageName)
            setState {
                copy(
                    name = displayName,
                    icon = provider.applicationIcon,
                )
            }
        }

        viewModelScope.launch(context = Dispatchers.Default) {
            interactor.watchSkuList { connected, list ->
                Timber.d("SKU list updated: $connected $list")
                setState {
                    copy(
                        connected = connected,
                        skuList = list,
                    )
                }
            }
        }

        viewModelScope.launch(context = Dispatchers.Default) {
            listener.watchErrors { error ->
                Timber.e(error, "Billing error received")
                setState { copy(error = error) }
            }
        }
    }

    override fun handleViewEvent(event: BillingDialogViewEvent) {
        return when (event) {
            is BillingDialogViewEvent.Close -> publish(BillingDialogControllerEvent.Close)
            is BillingDialogViewEvent.Purchase -> purchase(event.index)
            is BillingDialogViewEvent.ClearError -> setState { copy(error = null) }
        }
    }

    internal fun refresh() {
        viewModelScope.launch(context = Dispatchers.Default) {
            interactor.refresh()
        }
    }

    private fun purchase(index: Int) {
        val skuList = state.skuList
        if (skuList.isEmpty() || skuList.size <= index) {
            Timber.e("SKU index out of bounds: $index ${skuList.size}")
            setState { copy(error = BillingError("Unable to purchase in-app item")) }
            return
        }

        val sku = skuList[index]
        publish(BillingDialogControllerEvent.LaunchPurchase(sku))
    }

}

