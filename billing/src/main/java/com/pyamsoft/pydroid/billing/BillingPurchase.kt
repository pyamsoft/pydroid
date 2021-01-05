package com.pyamsoft.pydroid.billing

import android.app.Activity

/**
 * Purchase client
 */
public interface BillingPurchase {

    /**
     * Watch for errors in the billing client
     */
    public suspend fun watchErrors(onErrorReceived: (BillingError) -> Unit)

    /**
     * Purchase an in-app item
     */
    public suspend fun purchase(activity: Activity, sku: BillingSku)
}