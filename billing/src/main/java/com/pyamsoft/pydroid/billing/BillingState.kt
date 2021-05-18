package com.pyamsoft.pydroid.billing

/** State of the billing client */
public enum class BillingState {
  /** Billing client is still loading, state unknown */
  LOADING,

  /** Billing client is connected and active */
  CONNECTED,

  /** Billing client is currently disconnected but may become active again later. */
  DISCONNECTED
}
