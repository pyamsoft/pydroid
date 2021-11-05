package com.pyamsoft.pydroid.billing

import androidx.appcompat.app.AppCompatActivity

/** Abstracts the Play Store Billing client */
public interface BillingConnector {

  /**
   * Start the billing client
   *
   * Will automatically manage connections
   */
  public fun start(activity: AppCompatActivity)
}
