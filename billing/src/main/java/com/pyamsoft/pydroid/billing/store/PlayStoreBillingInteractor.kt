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

package com.pyamsoft.pydroid.billing.store

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsResult
import com.pyamsoft.pydroid.billing.AbstractBillingInteractor
import com.pyamsoft.pydroid.billing.BillingFlowState
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.core.cast
import com.pyamsoft.pydroid.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PlayStoreBillingInteractor
internal constructor(
  private val enforcer: ThreadEnforcer,
  context: Context,
  errorBus: EventBus<Throwable>,
) :
  AbstractBillingInteractor(
    context = context.applicationContext,
    errorBus = errorBus,
  ),
  BillingClientStateListener,
  PurchasesUpdatedListener,
  ConsumeResponseListener,
  ProductDetailsResponseListener {

  private val client by lazy {
    // Billing 7 change
    // https://developer.android.com/google/play/billing/release-notes#google_play_billing_library_700_release_2024-05-14
    val pendingPurchaseParams = PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()

    BillingClient.newBuilder(context.applicationContext)
      .setListener(this)
      .enablePendingPurchases(pendingPurchaseParams)
      // Auto service reconnection, Billing 8
      // https://developer.android.com/google/play/billing/migrate-gpblv8
      .enableAutoServiceReconnection()
      .build()
  }

  override suspend fun onClientConnect() {
    // The Billing library method is thread safe, so we can safely enforce being OMT
    enforcer.assertOffMainThread()

    if (!client.isReady) {
      Logger.d { "Connect to Billing Client" }

      // onBillingSetupFinished
      // onBillingServiceDisconnected
      client.startConnection(this)
    }
  }

  override fun onClientDisconnect() {
    Logger.d { "Disconnect from billing client" }
    client.endConnection()
  }

  private fun querySkus() {
    val skuList = getSkuList()
    Logger.d { "Querying for SKUs $skuList" }

    // Map this here every time since we do not know if the QPDP builder carries state that cannot
    // be re-used.
    val skus =
      skuList.map { sku ->
        QueryProductDetailsParams.Product.newBuilder()
          .setProductType(BillingClient.ProductType.INAPP)
          .setProductId(sku)
          .build()
      }

    val params = QueryProductDetailsParams.newBuilder().setProductList(skus).build()

    // onProductDetailsResponse
    client.queryProductDetailsAsync(params, this)
  }

  private fun consumePurchase(purchase: Purchase) {
    Logger.d { "Consume purchase: $purchase" }
    val params = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()

    // onConsumeResponse
    client.consumeAsync(params, this)
  }

  private fun handlePurchases(purchases: List<Purchase>) {
    for (purchase in purchases) {
      // We should only consume purchases once they are complete
      //
      // Since our app is fully local and purchases do not grant any additional features, they
      // serve only as Tips, we should only consume fully purchased items, and just ignore
      // pending or unknown state until the item completes purchase
      if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
        consumePurchase(purchase)
      }
    }
  }

  override fun onProductDetailsResponse(result: BillingResult, details: QueryProductDetailsResult) {
    // Billing 8 change delivers back "unfetched" products
    //
    // Since we can not do anything about unfetched products, just ignore them. YOLO
    // https://developer.android.com/google/play/billing/migrate-gpblv8
    if (result.isOk()) {
      val products = details.productDetailsList
      Logger.d { "Sku response: $products" }
      val skuList = products.map { PlayBillingSku(it) }
      emitSkuFlow(state = BillingFlowState(BillingState.CONNECTED, skuList))
    } else {
      Logger.w { "SKU response not OK: ${result.debugMessage}" }
      emitSkuFlow(state = BillingFlowState(BillingState.DISCONNECTED, emptyList()))
    }
  }

  override fun onConsumeResponse(result: BillingResult, token: String) {
    if (result.isOk()) {
      Logger.d { "Purchase consumed $token" }
    } else {
      launchInScope(context = Dispatchers.Default) {
        Logger.w { "Consume response not OK: ${result.debugMessage}" }
        emitError(RuntimeException(result.debugMessage))
      }
    }
  }

  override fun onBillingSetupFinished(result: BillingResult) {
    if (result.isOk()) {
      Logger.d { "Billing client is ready, query products!" }

      // Reset the backoff to 1
      resetBackoff()

      querySkus()
    } else {
      Logger.w { "Billing setup not OK: ${result.debugMessage}" }
      emitSkuFlow(state = BillingFlowState(BillingState.DISCONNECTED, emptyList()))
    }
  }

  override fun onBillingServiceDisconnected() {
    Logger.w { "Billing client was disconnected!" }
    onDisconnected()
  }

  override suspend fun onClientRefresh() =
    withContext(context = Dispatchers.Default) {
      if (!client.isReady) {
        Logger.w { "Client is not ready yet, so we are not refreshing sku and purchases" }
        return@withContext
      }

      querySkus()
    }

  override suspend fun onPurchase(activity: ComponentActivity, sku: BillingSku) =
    withContext(context = Dispatchers.Default) {
      val realSku = sku.cast<PlayBillingSku>()
      if (realSku == null) {
        emitError(ERROR_WRONG_SKU_TYPE)
        return@withContext
      }

      val products =
        listOf(
          BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(realSku.sku)
            // Do not need to set offerToken since we are not a subscription
            .build(),
        )

      val params = BillingFlowParams.newBuilder().setProductDetailsParamsList(products).build()

      withContext(context = Dispatchers.Main) {
        Logger.d { "Launch purchase flow ${realSku.id}" }

        // onPurchasesUpdated
        client.launchBillingFlow(activity, params)
      }

      return@withContext
    }

  override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
    if (result.isOk()) {
      if (purchases != null) {
        if (purchases.isEmpty()) {
          launchInScope(context = Dispatchers.Default) {
            Logger.w { "Purchase list was empty!" }
            emitError(ERROR_BAD_PURCHASE_LIST)
          }
        } else {
          Logger.d { "Purchase succeeded! $purchases" }
          handlePurchases(purchases)
        }
      } else {
        launchInScope(context = Dispatchers.Default) {
          Logger.w { "Purchase list was null!" }
          emitError(ERROR_BAD_PURCHASE_LIST)
        }
      }
    } else {
      if (result.isUserCancelled()) {
        Logger.d { "User has cancelled purchase flow." }
      } else {
        launchInScope(context = Dispatchers.Default) {
          Logger.w { "Purchase response not OK: ${result.debugMessage}" }
          emitError(RuntimeException(result.debugMessage))
        }
      }
    }
  }

  companion object {

    private val ERROR_WRONG_SKU_TYPE =
      IllegalArgumentException("SKU must be of type PlayBillingSku")
    private val ERROR_BAD_PURCHASE_LIST = RuntimeException("Unable to process your recent purchase")

    @JvmStatic
    @CheckResult
    private fun BillingResult.isUserCancelled(): Boolean {
      return this.responseCode == BillingClient.BillingResponseCode.USER_CANCELED
    }

    @JvmStatic
    @CheckResult
    private fun BillingResult.isOk(): Boolean {
      return this.responseCode == BillingClient.BillingResponseCode.OK
    }
  }
}
