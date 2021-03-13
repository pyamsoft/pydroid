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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.createViewBinder
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.databinding.ListitemLinearHorizontalBinding

internal class BillingViewHolder private constructor(
    binding: ListitemLinearHorizontalBinding,
    callback: BillingAdapter.Callback
) : RecyclerView.ViewHolder(binding.root), ViewBinder<BillingItemViewState> {

    private val binder: ViewBinder<BillingItemViewState>

    internal var clickView: BillingItemClick? = null
    internal var contentView: BillingItemContent? = null
    internal var priceView: BillingItemPrice? = null

    init {
        Injector.obtainFromApplication<PYDroidComponent>(itemView.context)
            .plusBillingItem()
            .create(binding.listitemLinearH)
            .inject(this)

        val click = requireNotNull(clickView)
        val content = requireNotNull(contentView)
        val price = requireNotNull(priceView)
        binder = createViewBinder(
            click,
            content,
            price
        ) {
            return@createViewBinder when (it) {
                is BillingItemViewEvent.Purchase -> callback.onPurchase(adapterPosition)
            }
        }
    }

    override fun bindState(state: BillingItemViewState) {
        binder.bindState(state)
    }

    override fun teardown() {
        binder.teardown()

        contentView = null
        priceView = null
    }

    companion object {

        @CheckResult
        @JvmStatic
        fun create(
            inflater: LayoutInflater,
            container: ViewGroup,
            callback: BillingAdapter.Callback,
        ): BillingViewHolder {
            val binding = ListitemLinearHorizontalBinding.inflate(inflater, container, false)
            return BillingViewHolder(binding, callback)
        }
    }
}
