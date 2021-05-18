package com.pyamsoft.pydroid.billing

/** Abstracts the Play Store Billing client */
public interface BillingConnector {

  /** Connect the billing client */
  public fun connect()

  /** Disconnect the billing client */
  public fun disconnect()
}
