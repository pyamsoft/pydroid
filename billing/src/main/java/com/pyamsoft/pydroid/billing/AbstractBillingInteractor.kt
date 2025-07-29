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

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal abstract class AbstractBillingInteractor
protected constructor(
  context: Context,
  private val errorBus: EventBus<Throwable>,
) : BillingConnector, BillingInteractor, BillingLauncher {

  private val appSkuList: List<String>

  private val skuFlow: MutableStateFlow<BillingFlowState> =
    MutableStateFlow(
      BillingFlowState(
        state = BillingState.LOADING,
        list = emptyList(),
      ),
    )

  private val billingScope = MainScope()

  private var backoffCount = 1

  init {
    Logger.d { "Construct new interactor and billing client" }

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

  private fun disconnectBillingClient() {
    onClientDisconnect()
    billingScope.cancel()
  }

  final override fun bind(activity: ComponentActivity) {
    activity.lifecycle.doOnCreate {
      Logger.d { "Attempt to connect Billing on Activity create" }
      activity.lifecycleScope.launch(context = Dispatchers.Default) { onClientConnect() }
    }

    activity.lifecycle.doOnDestroy {
      Logger.d { "Attempt disconnect Billing on Activity destroy" }
      disconnectBillingClient()
    }
  }

  protected fun onDisconnected() {
    Logger.w { "Billing client was disconnected!" }

    billingScope.launch(context = Dispatchers.Default) {
      val waitTime = backoffCount
      backoffCount *= 2

      if (backoffCount < 1024) {
        Logger.d { "Wait to reconnect for $waitTime seconds" }
        delay(waitTime.seconds)

        withContext(context = Dispatchers.Default) {
          Logger.d { "Try connecting again" }
          onClientConnect()
        }
      } else {
        Logger.w { "We have tried to connect and have been unsuccessful. Billing DISABLED" }
      }
    }
  }

  final override suspend fun refresh() =
    withContext(context = Dispatchers.Default) { onClientRefresh() }

  final override fun watchSkuList(): Flow<BillingInteractor.BillingSkuListSnapshot> =
    skuFlow.map {
      BillingInteractor.BillingSkuListSnapshot(
        status = it.state,
        skus = it.list,
      )
    }

  final override suspend fun purchase(activity: ComponentActivity, sku: BillingSku): Unit =
    withContext(context = Dispatchers.Default) {
      try {
        onPurchase(activity, sku)
      } catch (e: Throwable) {
        Logger.e(e) { "Failed purchase flow for SKU: $sku" }
        emitError(RuntimeException(e.message ?: "An error occurred during purchasing."))
      }
    }

  final override fun watchBillingErrors(): Flow<Throwable> =
    errorBus.onEach { Logger.e(it) { "Billing error received!" } }

  @CheckResult
  protected fun getSkuList(): List<String> = appSkuList

  protected abstract suspend fun onClientConnect()

  protected abstract fun onClientDisconnect()

  protected abstract suspend fun onPurchase(activity: ComponentActivity, sku: BillingSku)

  protected abstract suspend fun onClientRefresh()

  protected fun resetBackoff() {
    backoffCount = 1
  }

  protected suspend fun emitError(throwable: Throwable) {
    errorBus.emit(throwable)
  }

  protected fun emitSkuFlow(state: BillingFlowState) {
    skuFlow.value = state
  }

  protected fun launchInScope(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ) = billingScope.launch(context, start, block)

  companion object {

    private const val DEV_SUFFIX = ".dev"
  }
}
