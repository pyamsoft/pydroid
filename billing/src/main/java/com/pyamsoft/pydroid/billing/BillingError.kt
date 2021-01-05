package com.pyamsoft.pydroid.billing

/**
 * A Billing error
 */
public data class BillingError(override val message: String) : RuntimeException(message)
