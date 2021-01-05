package com.pyamsoft.pydroid.billing

import com.android.billingclient.api.SkuDetails

internal data class PlayBillingSku internal constructor(
    internal val sku: SkuDetails
) : BillingSku {

    override val id: String = sku.sku

    override val price: String = sku.price

    override val title: String = sku.title

    override val description: String = sku.description

    override val iconUrl: String = sku.iconUrl
}