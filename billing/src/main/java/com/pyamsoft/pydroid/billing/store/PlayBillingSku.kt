/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.billing.store

import com.android.billingclient.api.SkuDetails
import com.pyamsoft.pydroid.billing.BillingSku

internal data class PlayBillingSku internal constructor(internal val sku: SkuDetails) : BillingSku {

  override val id: String = sku.sku

  override val displayPrice: String = sku.price

  override val price: Long = sku.priceAmountMicros

  override val title: String = sku.title

  override val description: String = sku.description

  override val iconUrl: String = sku.iconUrl
}
