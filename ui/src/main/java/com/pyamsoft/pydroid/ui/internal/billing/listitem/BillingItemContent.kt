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
import com.pyamsoft.pydroid.ui.databinding.BillingItemContentBinding

internal class BillingItemContent internal constructor(parent: ViewGroup) :
    BaseUiView<BillingItemViewState, Nothing, BillingItemContentBinding>(parent) {

  override val viewBinding = BillingItemContentBinding::inflate

  override val layoutRoot by boundView { billingItemContentRoot }

  init {
    doOnTeardown { clear() }
  }

  private fun clear() {
    binding.billingItemTitle.text = ""
    binding.billingItemDescription.text = ""
  }

  override fun onRender(state: UiRender<BillingItemViewState>) {
    state.mapChanged { it.sku }.render(viewScope) { handleSku(it) }
  }

  private fun handleSku(sku: BillingSku) {
    binding.billingItemTitle.text = sku.title
    binding.billingItemDescription.text = sku.description
  }
}
