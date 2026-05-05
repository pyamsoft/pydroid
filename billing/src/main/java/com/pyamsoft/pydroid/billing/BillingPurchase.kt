/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.billing

/** A representation of a successful billing purchase transaction */
public sealed interface BillingPurchase {

  /** Real purchase transaction event from the Play Billing library */
  @ConsistentCopyVisibility
  public data class PlayBillingConsumed
  internal constructor(
      val purchaseToken: String,
  ) : BillingPurchase

  /** Fake success from the billing test when running in debug mode */
  @ConsistentCopyVisibility
  public data class Fake
  internal constructor(
      val sku: BillingSku,
  ) : BillingPurchase
}
