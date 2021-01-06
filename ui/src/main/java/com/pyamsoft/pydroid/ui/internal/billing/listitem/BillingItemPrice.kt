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

package com.pyamsoft.pydroid.ui.internal.billing.listitem

import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.ui.databinding.BillingItemPriceBinding

internal class BillingItemPrice internal constructor(
    parent: ViewGroup
) : BaseUiView<BillingItemViewState, Nothing, BillingItemPriceBinding>(parent) {

    override val viewBinding = BillingItemPriceBinding::inflate

    override val layoutRoot by boundView { billingItemPriceRoot }

    init {
        doOnTeardown {
            clear()
        }
    }

    private fun clear() {
        binding.billingItemPrice.text = ""
    }

    override fun onRender(state: UiRender<BillingItemViewState>) {
        state.distinctBy { it.sku }.render(viewScope) { handleSku(it) }
    }

    private fun handleSku(sku: BillingSku) {
        binding.billingItemPrice.text = sku.displayPrice
    }
}
