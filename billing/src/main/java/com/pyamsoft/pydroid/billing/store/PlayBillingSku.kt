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

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.SkuDetails
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.core.requireNotNull

internal data class PlayBillingSku internal constructor(internal val sku: ProductDetails) : BillingSku {

  private val product = sku.oneTimePurchaseOfferDetails.requireNotNull()

  override val id: String = sku.productId

  override val displayPrice: String = product.formattedPrice

  override val price: Long = product.priceAmountMicros

  override val title: String = sku.title

  override val description: String = sku.description
}
