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

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.billing.AbstractBillingInteractor
import com.pyamsoft.pydroid.billing.BillingFlowState
import com.pyamsoft.pydroid.billing.BillingPurchase
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.LintIgnoreEmptyFunctionBlock
import com.pyamsoft.pydroid.util.Logger
import kotlinx.coroutines.Dispatchers

internal class FakeBillingInteractor
internal constructor(
    context: Context,
    errorBus: EventBus<Throwable>,
    purchaseBus: EventBus<BillingPurchase>,
) :
    AbstractBillingInteractor(
        context = context,
        errorBus = errorBus,
        purchaseBus = purchaseBus,
    ) {

  @CheckResult
  private fun makeFakeSku(priceInDollars: Long): BillingSku =
      FakeBillingSku(
          title = "Fake Buy $${priceInDollars}",
          description = "Fake purchase for $${priceInDollars}",
          priceInCents = priceInDollars * 100,
      )

  @LintIgnoreEmptyFunctionBlock override suspend fun onClientConnect() {}

  @LintIgnoreEmptyFunctionBlock override fun onClientDisconnect() {}

  override suspend fun onPurchase(activity: ComponentActivity, sku: BillingSku) {
    launchInScope(context = Dispatchers.Default) {
      if (sku.price > FAIL_PURCHASE_OVER) {
        Logger.w { "Purchase response not OK: $sku" }
        emitError(RuntimeException("Error purchasing ${sku.title}"))
      } else {
        Logger.d { "Purchase success $sku" }
        emitPurchase(BillingPurchase.Fake(sku))
      }
    }
  }

  override suspend fun onClientRefresh() {
    // Fake a list of products to purchase
    emitStateUpdate(
        state =
            BillingFlowState(
                state = BillingState.CONNECTED,
                list =
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

  companion object {

    private const val FAIL_PURCHASE_OVER: Long = 30_000_000
  }
}
