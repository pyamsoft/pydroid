package com.pyamsoft.pydroid.billing

/**
 * Abstracts the Play Store Billing client
 */
public interface BillingInteractor {

    /**
     * Get the list of SKU
     */
    public suspend fun watchSkuList(onSkuListReceived: (BillingState, List<BillingSku>) -> Unit)

    /**
     * Watch for errors in the billing client
     */
    public suspend fun watchErrors(onErrorReceived: (BillingError) -> Unit)

    /**
     * Refresh the SKU list
     */
    public suspend fun refresh()
}