package com.pyamsoft.pydroid.billing

/**
 * Abstracts the Play Store Billing client
 */
public interface BillingInteractor {

    /**
     * Get the list of SKU
     */
    public suspend fun watchSkuList(onSkuListReceived: (BillingState, List<BillingSku>) -> Unit)
}