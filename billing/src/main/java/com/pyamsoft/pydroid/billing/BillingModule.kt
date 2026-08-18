/*
 * Copyright 2026 pyamsoft
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

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.billing.fake.FakeBillingInteractor
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.util.AppDispatchers

/** Billing module */
public abstract class BillingModule(params: Parameters) {

  private val connector: BillingConnector

  init {
    connector =
        when (params.mode) {
          BillingMode.FAKE ->
              FakeBillingInteractor(
                  errorBus = params.errorBus,
                  purchaseBus = params.purchaseBus,
                  dispatchers = params.dispatchers,
              )
          BillingMode.REAL -> newConnector(params)
        }
  }

  /** Provide a connector instance */
  @CheckResult
  public fun provideConnector(): BillingConnector {
    return connector
  }

  /** Is this a "live" client, or is it no-op */
  @CheckResult public abstract fun isLive(): Boolean

  @CheckResult protected abstract fun newConnector(params: Parameters): BillingConnector

  /** Module parameters */
  public data class Parameters(
      val enforcer: ThreadEnforcer,
      val errorBus: EventBus<Throwable>,
      val purchaseBus: EventBus<BillingPurchase>,
      val dispatchers: AppDispatchers,
      internal val mode: BillingMode,
  )
}
