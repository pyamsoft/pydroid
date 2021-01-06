package com.pyamsoft.pydroid.billing

/**
 * A Billing error
 */
internal data class BillingError internal constructor(
    /**
     * The billing error message
     */
    override val message: String
) : RuntimeException(message)
