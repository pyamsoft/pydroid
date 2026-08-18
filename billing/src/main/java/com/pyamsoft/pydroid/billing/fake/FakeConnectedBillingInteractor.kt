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

package com.pyamsoft.pydroid.billing.fake

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.billing.AbstractConnectedBillingInteractor
import com.pyamsoft.pydroid.billing.BillingFlowState
import com.pyamsoft.pydroid.billing.BillingPurchase
import com.pyamsoft.pydroid.billing.BillingPurchase.Fake
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.LintIgnoreEmptyFunctionBlock
import com.pyamsoft.pydroid.util.AppDispatchers
import com.pyamsoft.pydroid.util.Logger

internal class FakeConnectedBillingInteractor
internal constructor(
    activity: ComponentActivity,
    errorBus: EventBus<Throwable>,
    purchaseBus: EventBus<BillingPurchase>,
    dispatchers: AppDispatchers,
) :
    AbstractConnectedBillingInteractor(
        activity = activity,
        errorBus = errorBus,
        purchaseBus = purchaseBus,
        dispatchers,
    ) {

  @CheckResult
  private fun makeFakeSku(priceInDollars: Long): BillingSku =
      FakeBillingSku(
          title = "Fake Buy $${priceInDollars}",
          description = "Fake purchase for $${priceInDollars}",
          priceInCents = priceInDollars * 100,
      )

  override suspend fun onPurchase(activity: ComponentActivity, sku: BillingSku) {
    launchInScope(context = dispatchers.default) {
      if (sku.price > FAIL_PURCHASE_OVER) {
        Logger.w { "Purchase response not OK: $sku" }
        emitError(RuntimeException("Error purchasing ${sku.title}"))
      } else {
        Logger.d { "Purchase success $sku" }
        emitPurchase(Fake(sku))
      }
    }
  }

  override suspend fun onClientRefresh() {
    // Fake a list of products to purchase
    emitStateUpdate(
        state =
            BillingFlowState(
                status = BillingState.CONNECTED,
                skus =
                    listOf(
                        makeFakeSku(priceInDollars = 1),
                        makeFakeSku(priceInDollars = 3),
                        makeFakeSku(priceInDollars = 5),
                        makeFakeSku(priceInDollars = 10),
                        makeFakeSku(priceInDollars = 15),
                        makeFakeSku(priceInDollars = 20),
                        makeFakeSku(priceInDollars = 30),
                        makeFakeSku(priceInDollars = 50),
                        makeFakeSku(priceInDollars = 100),
                        makeFakeSku(priceInDollars = 250),
                        makeFakeSku(priceInDollars = 300),
                        makeFakeSku(priceInDollars = 500),
                    ),
            ),
    )
  }

  @LintIgnoreEmptyFunctionBlock override suspend fun onClientConnect() {}

  @LintIgnoreEmptyFunctionBlock override fun onClientDisconnect() {}

  companion object {

    private const val FAIL_PURCHASE_OVER: Long = 30_000_000
  }
}
