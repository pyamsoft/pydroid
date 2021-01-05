package com.pyamsoft.pydroid.billing

/**
 * Purchase client
 */
public interface BillingPurchaseListener {

    /**
     * Watch for errors in the billing client
     */
    public suspend fun watchErrors(onErrorReceived: (BillingError) -> Unit)
}