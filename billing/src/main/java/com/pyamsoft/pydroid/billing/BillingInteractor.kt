package com.pyamsoft.pydroid.billing

import android.app.Activity

/**
 * Abstracts the Play Store Billing client
 */
public interface BillingInteractor {

    /**
     * Connect the billing client
     */
    public fun connect()

    /**
     * Disconnect the billing client
     */
    public fun disconnect()

    /**
     * Watch for errors in the billing client
     */
    public fun watchErrors(onErrorReceived: (BillingError) -> Unit)

    /**
     * Get the list of SKU
     */
    public fun watchSkuList(onSkuListReceived: (List<BillingSku>) -> Unit)

    /**
     * Purchase an in-app item
     */
    public fun purchase(activity: Activity, sku: BillingSku)
}