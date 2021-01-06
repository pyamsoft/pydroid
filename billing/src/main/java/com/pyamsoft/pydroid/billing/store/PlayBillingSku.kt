package com.pyamsoft.pydroid.billing.store

import com.android.billingclient.api.SkuDetails
import com.pyamsoft.pydroid.billing.BillingSku

internal data class PlayBillingSku internal constructor(
    internal val sku: SkuDetails
) : BillingSku {

    override val id: String = sku.sku

    override val displayPrice: String = sku.price

    override val price: Long = sku.priceAmountMicros

    override val title: String = sku.title

    override val description: String = sku.description

    override val iconUrl: String = sku.iconUrl
}