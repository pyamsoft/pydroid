package com.pyamsoft.pydroid.billing

import android.app.Activity

/** Purchase client */
public interface BillingLauncher {

  /** Purchase an in-app item */
  public suspend fun purchase(activity: Activity, sku: BillingSku)
}
