package com.pyamsoft.pydroid.billing

/**
 * A Billing error
 */
public data class BillingError(
    /**
     * The billing error message
     */
    override val message: String
) : RuntimeException(message)
