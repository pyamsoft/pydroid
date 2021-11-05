package com.pyamsoft.pydroid.billing.store

import android.app.Activity
import android.content.Context
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.pyamsoft.pydroid.billing.BillingConnector
import com.pyamsoft.pydroid.billing.BillingInteractor
import com.pyamsoft.pydroid.billing.BillingLauncher
import com.pyamsoft.pydroid.billing.BillingSku
import com.pyamsoft.pydroid.billing.BillingState
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PlayStoreBillingInteractor
internal constructor(context: Context, private val errorBus: EventBus<Throwable>) :
    BillingInteractor,
    BillingConnector,
    BillingLauncher,
    BillingClientStateListener,
    SkuDetailsResponseListener,
    ConsumeResponseListener,
    PurchasesUpdatedListener {

  private val client by lazy {
    BillingClient.newBuilder(context.applicationContext)
        .setListener(this)
        .enablePendingPurchases()
        .build()
  }

  private val appSkuList: List<String>

  private val skuFlow = MutableStateFlow(State(BillingState.LOADING, emptyList()))

  private val billingScope = MainScope()

  private var backoffCount = 1

  init {
    Logger.d("Construct new interactor and billing client")

    val rawPackageName = context.applicationContext.packageName
    val packageName =
        if (rawPackageName.endsWith(DEV_SUFFIX))
            rawPackageName.substring(0 until rawPackageName.length - DEV_SUFFIX.length)
        else rawPackageName
    appSkuList =
        listOf(
            "$packageName.iap_one",
            "$packageName.iap_three",
            "$packageName.iap_five",
            "$packageName.iap_ten",
        )
  }

  override fun start(activity: AppCompatActivity) {
    activity.lifecycle.doOnCreate {
      Logger.d("Attempt to connect Billing on Activity create")
      connect()
    }

    activity.lifecycle.doOnDestroy {
      Logger.d("Attempt disconnect Billing on Activity destroy")
      disconnect()
    }
  }

  private fun connect() {
    if (!client.isReady) {
      Logger.d("Connect to Billing Client")
      client.startConnection(this)
    }
  }

  private fun disconnect() {
    Logger.d("Disconnect from billing client")
    client.endConnection()

    billingScope.cancel()
  }

  override fun onBillingSetupFinished(result: BillingResult) {
    if (result.isOk()) {
      Logger.d("Billing client is ready, query products!")

      // Reset the backoff to 1
      backoffCount = 1

      querySkus()
    } else {
      Logger.w("Billing setup not OK: ${result.debugMessage}")
      skuFlow.value = State(BillingState.DISCONNECTED, emptyList())
    }
  }

  private fun querySkus() {
    Logger.d("Querying for SKUs $appSkuList")

    val params =
        SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(appSkuList)
            .build()

    client.querySkuDetailsAsync(params, this)
  }

  override suspend fun refresh() =
      withContext(context = Dispatchers.Main) {
        if (!client.isReady) {
          Logger.w("Client is not ready yet, so we are not refreshing sku and purchases")
          return@withContext
        }

        querySkus()
      }

  override fun onSkuDetailsResponse(result: BillingResult, skuDetails: MutableList<SkuDetails>?) {
    if (result.isOk()) {
      Logger.d("Sku response: $skuDetails")
      billingScope.launch(context = Dispatchers.IO) {
        val skuList = skuDetails?.map { PlayBillingSku(it) } ?: emptyList()
        skuFlow.value = State(BillingState.CONNECTED, skuList)
      }
    } else {
      Logger.w("SKU response not OK: ${result.debugMessage}")
      skuFlow.value = State(BillingState.DISCONNECTED, emptyList())
    }
  }

  override suspend fun watchSkuList(onSkuListReceived: (BillingState, List<BillingSku>) -> Unit) =
      withContext(context = Dispatchers.IO) {
        skuFlow.collect { event -> onSkuListReceived(event.state, event.list) }
      }

  override fun onBillingServiceDisconnected() {
    Logger.w("Billing client was disconnected!")

    billingScope.launch(context = Dispatchers.Default) {
      val waitTime = 1000L * backoffCount
      backoffCount *= 2

      Logger.d("Wait to reconnect for $waitTime milliseconds")
      delay(waitTime)

      Logger.d("Try connecting again")
      connect()
    }
  }

  override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
    if (result.isOk()) {
      if (purchases != null) {
        Logger.d("Purchase succeeded! $purchases")
        handlePurchases(purchases)
      } else {
        Logger.w("Purchase list was null!")
      }
    } else {
      if (result.isUserCancelled()) {
        Logger.d("User has cancelled purchase flow.")
      } else {
        billingScope.launch(context = Dispatchers.IO) {
          Logger.w("Purchase response not OK: ${result.debugMessage}")
          errorBus.send(RuntimeException(result.debugMessage))
        }
      }
    }
  }

  override suspend fun purchase(activity: Activity, sku: BillingSku): Unit =
      withContext(context = Dispatchers.Default) {
        val billingSku = sku as PlayBillingSku
        val params = BillingFlowParams.newBuilder().setSkuDetails(billingSku.sku).build()

        withContext(context = Dispatchers.Main) {
          Logger.d("Launch purchase flow ${sku.id}")
          client.launchBillingFlow(activity, params)
        }
      }

  override suspend fun watchErrors(onErrorReceived: (Throwable) -> Unit) =
      withContext(context = Dispatchers.IO) { errorBus.onEvent { onErrorReceived(it) } }

  private fun handlePurchases(purchases: List<Purchase>) {
    for (purchase in purchases) {
      Logger.d("Consume purchase: $purchase")
      val params = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
      client.consumeAsync(params, this)
    }
  }

  override fun onConsumeResponse(result: BillingResult, token: String) {
    if (result.isOk()) {
      Logger.d("Purchase consumed $token")
    } else {
      Logger.w("Consume response not OK: ${result.debugMessage}")
    }
  }

  private data class State(val state: BillingState, val list: List<BillingSku>)

  companion object {

    private const val DEV_SUFFIX = ".dev"

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
