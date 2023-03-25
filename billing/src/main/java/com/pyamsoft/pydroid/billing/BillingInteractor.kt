/*
 * Copyright 2023 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

/** Abstracts the Play Store Billing client */
public interface BillingInteractor {

  /** Get the list of SKU */
  public suspend fun watchSkuList(onSkuListReceived: (BillingState, List<BillingSku>) -> Unit)

  /** Watch for errors in the billing client */
  public suspend fun watchErrors(onErrorReceived: (Throwable) -> Unit)

  /** Refresh the SKU list */
  public suspend fun refresh()
}
