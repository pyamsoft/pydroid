package com.pyamsoft.pydroid.billing.store

import androidx.annotation.CheckResult
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult

@CheckResult
internal fun BillingResult.isUserCancelled(): Boolean {
    return this.responseCode == BillingClient.BillingResponseCode.USER_CANCELED
}

@CheckResult
internal fun BillingResult.isOk(): Boolean {
    return this.responseCode == BillingClient.BillingResponseCode.OK
}