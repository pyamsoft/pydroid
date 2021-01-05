package com.pyamsoft.pydroid.billing

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class PlayStoreBillingInteractor internal constructor(
    activity: Activity
) : BillingInteractor,
    PurchasesUpdatedListener,
    BillingClientStateListener,
    ConsumeResponseListener {

    private val client by lazy {
        BillingClient.newBuilder(activity)
            .setListener(this)
            .enablePendingPurchases()
            .build()
    }

    private val appSkuList: List<String>

    private val skuFlow = MutableStateFlow<List<BillingSku>>(emptyList())

    private val errorBus = MutableSharedFlow<BillingError>()

    private val billingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var backoffCount = 1

    init {
        val packageName = activity.applicationContext.packageName
        appSkuList = listOf(
            "$packageName-ONE",
            "$packageName-THREE",
            "$packageName-FIVE",
            "$packageName-TEN",
        )
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.isOk()) {
            if (purchases != null) {
                Timber.d("Purchase succeeded! $purchases")
                handlePurchases(purchases)
            } else {
                Timber.w("Purchase list was null!")
            }
        } else {
            if (result.isUserCancelled()) {
                Timber.d("User has cancelled purchase flow.")
            } else {
                Timber.w("Purchase response not OK: ${result.debugMessage}")
                billingScope.launch(context = Dispatchers.IO) {
                    errorBus.emit(BillingError(result.debugMessage))
                }
            }
        }
    }

    override fun watchErrors(onErrorReceived: (BillingError) -> Unit) {
        billingScope.launch(context = Dispatchers.IO) {
            errorBus.collect {
                withContext(context = Dispatchers.Main) {
                    onErrorReceived(it)
                }
            }
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            Timber.d("Consume purchase: $purchase")
            val params = ConsumeParams
                .newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            client.consumeAsync(params, this)
        }
    }

    override fun onConsumeResponse(result: BillingResult, token: String) {
        if (result.isOk()) {
            Timber.d("Purchase consumed $token")
        } else {
            Timber.w("Consume response not OK: ${result.debugMessage}")
        }
    }

    override fun connect() {
        val self = this
        billingScope.launch(context = Dispatchers.Main) {
            if (!client.isReady) {
                Timber.d("Connect to Billing Client")
                client.startConnection(self)
            }
        }
    }

    override fun disconnect() {
        if (client.isReady) {
            Timber.d("Disconnect from billing client")
            client.endConnection()

            billingScope.cancel()
            backoffCount = 1
        }
    }

    @ExperimentalCoroutinesApi
    override fun onBillingSetupFinished(result: BillingResult) {
        if (result.isOk()) {
            Timber.d("Billing client is ready, query products!")
            querySkus()
        } else {
            Timber.w("Billing setup not OK: ${result.debugMessage}")
        }
    }

    private fun querySkus() {
        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(appSkuList)
            .build()

        client.querySkuDetailsAsync(params) { result, skuDetailsList ->
            if (result.isOk()) {
                billingScope.launch(context = Dispatchers.IO) {
                    val skuList = skuDetailsList?.map { PlayBillingSku(it) } ?: emptyList()
                    skuFlow.value = skuList
                }
            } else {
                Timber.w("SKU response not OK: ${result.debugMessage}")
            }
        }
    }

    override fun watchSkuList(onSkuListReceived: (List<BillingSku>) -> Unit) {
        billingScope.launch(context = Dispatchers.IO) {
            skuFlow.collect { list ->
                withContext(context = Dispatchers.Main) {
                    onSkuListReceived(list)
                }
            }
        }
    }

    override fun purchase(activity: Activity, sku: BillingSku) {
        billingScope.launch(context = Dispatchers.Default) {
            val billingSku = sku as PlayBillingSku
            val params = BillingFlowParams.newBuilder()
                .setSkuDetails(billingSku.sku)
                .build()

            withContext(context = Dispatchers.Main) {
                Timber.d("Launch purchase flow ${sku.id}")
                client.launchBillingFlow(activity, params)
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        Timber.e("Billing client was disconnected!")

        billingScope.launch(context = Dispatchers.Default) {
            val waitTime = 1000L * backoffCount
            backoffCount *= 2

            Timber.d("Wait to reconnect for $waitTime milliseconds")
            delay(waitTime)

            Timber.d("Try connecting again")
            connect()
        }
    }
}