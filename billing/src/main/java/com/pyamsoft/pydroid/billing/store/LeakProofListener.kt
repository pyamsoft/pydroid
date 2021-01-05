package com.pyamsoft.pydroid.billing.store

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import timber.log.Timber
import java.lang.ref.WeakReference

internal class LeakProofListener internal constructor(
    listeners: PlayStoreListeners
) : PlayStoreListeners {

    // Keep a reference to the actual listener as a weak reference.
    // https://stackoverflow.com/questions/65180072/android-billing-client-causes-memory-leak
    private val ref = WeakReference(listeners)

    private inline fun withRef(block: (PlayStoreListeners) -> Unit) {
        ref.get().let { strong ->
            if (strong == null) {
                Timber.w("WeakReference for Play Store Listeners was requested, but was null!")
            } else {
                block(strong)
            }
        }
    }

    override fun onBillingSetupFinished(result: BillingResult) {
        withRef { it.onBillingSetupFinished(result) }
    }

    override fun onBillingServiceDisconnected() {
        withRef { it.onBillingServiceDisconnected() }
    }

    override fun onConsumeResponse(result: BillingResult, token: String) {
        withRef { it.onConsumeResponse(result, token) }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        withRef { it.onPurchasesUpdated(result, purchases) }
    }

    override fun onSkuDetailsResponse(
        result: BillingResult,
        skuDetails: MutableList<SkuDetails>?
    ) {
        withRef { it.onSkuDetailsResponse(result, skuDetails) }
    }

}