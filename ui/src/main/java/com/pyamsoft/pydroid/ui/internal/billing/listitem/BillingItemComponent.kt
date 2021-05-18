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
import androidx.annotation.CheckResult

internal interface BillingItemComponent {

  fun inject(viewHolder: BillingViewHolder)

  interface Factory {

    @CheckResult fun create(parent: ViewGroup): BillingItemComponent
  }

  class Impl
  private constructor(
      private val parent: ViewGroup,
  ) : BillingItemComponent {

    override fun inject(viewHolder: BillingViewHolder) {
      val content = BillingItemContent(parent)
      val price = BillingItemPrice(parent)
      val click = BillingItemClick(parent)
      viewHolder.clickView = click
      viewHolder.contentView = content
      viewHolder.priceView = price
    }

    class FactoryImpl internal constructor() : Factory {

      override fun create(parent: ViewGroup): BillingItemComponent {
        return Impl(parent)
      }
    }
  }
}
