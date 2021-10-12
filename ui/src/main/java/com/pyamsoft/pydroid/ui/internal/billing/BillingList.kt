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

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.databinding.BillingListBinding
import com.pyamsoft.pydroid.ui.internal.billing.listitem.BillingAdapter
import com.pyamsoft.pydroid.ui.internal.billing.listitem.BillingItemViewState
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import com.pyamsoft.pydroid.util.asDp
import io.cabriole.decorator.LinearMarginDecoration

internal class BillingList
internal constructor(private val owner: LifecycleOwner, parent: ViewGroup) :
    BaseUiView<BillingViewState, BillingViewEvent, BillingListBinding>(parent),
    BillingAdapter.Callback {

  override val viewBinding = BillingListBinding::inflate

  override val layoutRoot by boundView { billingListRoot }

  private var billingAdapter: BillingAdapter? = null

  init {
    doOnInflate { setupListView() }

    doOnTeardown {
      binding.billingList.adapter = null
      billingAdapter = null
    }

    doOnInflate {
      val margin = 8.asDp(binding.billingList.context)
      LinearMarginDecoration.create(margin = margin).apply {
        binding.billingList.addItemDecoration(this)
      }
    }

    doOnTeardown { binding.billingList.removeAllItemDecorations() }
  }

  private fun setupListView() {
    billingAdapter = BillingAdapter(owner, this)

    binding.billingList.apply {
      adapter = billingAdapter
      layoutManager =
          LinearLayoutManager(context).apply {
            initialPrefetchItemCount = 3
            isItemPrefetchEnabled = false
          }
    }
  }

  override fun onRender(state: UiRender<BillingViewState>) {
    state.render(viewScope) { handleSkus(it) }
    state.mapChanged { it.error }.render(viewScope) { handleError(it) }
  }

  private fun handleError(throwable: Throwable?) {
    if (throwable != null) {
      Snackbreak.bindTo(owner) {
        val msg = throwable.message
        short(
            layoutRoot,
            if (msg.isNullOrBlank()) "Error during purchase flow." else msg,
            onHidden = { _, _ -> publish(BillingViewEvent.ClearError) })
      }
    }
  }

  private fun handleSkus(state: BillingViewState) {
    val billingState = state.connected
    val skuList = state.skuList
    if (billingState == BillingState.LOADING) {
      binding.billingList.isInvisible = true
      binding.billingError.isInvisible = true
    } else {
      if (billingState == BillingState.DISCONNECTED || skuList.isEmpty()) {
        clear()
      } else {
        loadSkus(skuList)
      }
    }
  }

  @CheckResult
  private fun usingAdapter(): BillingAdapter {
    return billingAdapter.requireNotNull()
  }

  private fun loadSkus(skuList: List<BillingSku>) {
    usingAdapter().submitList(skuList.map { BillingItemViewState(it) })

    binding.billingList.isVisible = true
    binding.billingError.isGone = true
  }

  private fun clear() {
    usingAdapter().submitList(null)

    binding.billingList.isGone = true
    binding.billingError.isVisible = true
  }

  override fun onPurchase(index: Int) {
    publish(BillingViewEvent.Purchase(index))
  }
}
