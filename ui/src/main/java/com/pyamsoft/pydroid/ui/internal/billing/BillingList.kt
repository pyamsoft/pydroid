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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.ui.databinding.ChangelogListBinding
import com.pyamsoft.pydroid.ui.internal.billing.listitem.BillingAdapter
import com.pyamsoft.pydroid.ui.internal.billing.listitem.BillingItemViewState
import com.pyamsoft.pydroid.ui.util.removeAllItemDecorations
import com.pyamsoft.pydroid.util.asDp
import io.cabriole.decorator.LinearMarginDecoration

internal class BillingList internal constructor(
    parent: ViewGroup
) : BaseUiView<BillingDialogViewState, BillingDialogViewEvent, ChangelogListBinding>(parent) {

    override val viewBinding = ChangelogListBinding::inflate

    override val layoutRoot by boundView { changelogList }

    private var billingAdapter: BillingAdapter? = null

    init {
        doOnInflate {
            setupListView()
        }

        doOnTeardown {
            binding.changelogList.adapter = null
            billingAdapter = null
        }

        doOnInflate {
            val margin = 8.asDp(binding.changelogList.context)
            LinearMarginDecoration.create(margin = margin).apply {
                binding.changelogList.addItemDecoration(this)
            }
        }

        doOnTeardown {
            binding.changelogList.removeAllItemDecorations()
        }
    }

    private fun setupListView() {
        billingAdapter = BillingAdapter {
            publish(BillingDialogViewEvent.Purchase(it))
        }

        binding.changelogList.apply {
            adapter = billingAdapter
            layoutManager = LinearLayoutManager(context).apply {
                initialPrefetchItemCount = 3
                isItemPrefetchEnabled = false
            }
        }
    }

    override fun onRender(state: UiRender<BillingDialogViewState>) {
        state.distinctBy { it.skuList }.render(viewScope) { skuList ->
            handleLoading(skuList)
            handleSkus(skuList)
        }
    }

    private fun handleLoading(skuList: List<BillingSku>) {
        if (skuList.isEmpty()) {
            hide()
        } else {
            show()
        }
    }

    private fun handleSkus(skuList: List<BillingSku>) {
        if (skuList.isEmpty()) {
            clear()
        } else {
            loadSkus(skuList)
        }
    }

    private fun show() {
        layoutRoot.isVisible = true
    }

    private fun hide() {
        layoutRoot.isVisible = false
    }

    private fun loadSkus(skuList: List<BillingSku>) {
        requireNotNull(billingAdapter).submitList(skuList.map { BillingItemViewState(it) })
    }

    private fun clear() {
        requireNotNull(billingAdapter).submitList(null)
    }
}
