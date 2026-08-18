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

import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.LintIgnoreTooGenericExceptionCaught
import com.pyamsoft.pydroid.core.LintIgnoreTooManyFunctions
import com.pyamsoft.pydroid.util.AppDispatchers
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.MarketLinker
import com.pyamsoft.pydroid.util.ifNotCancellation
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@LintIgnoreTooManyFunctions
public abstract class AbstractConnectedBillingInteractor
protected constructor(
    activity: ComponentActivity,
    private val errorBus: EventBus<Throwable>,
    private val purchaseBus: EventBus<BillingPurchase>,
    protected val dispatchers: AppDispatchers,
) : ConnectedBillingInteractor {

  private val appSkuList: List<String>

  private val skuFlow: MutableStateFlow<BillingFlowState> =
      MutableStateFlow(
          BillingFlowState(
              status = BillingState.LOADING,
              skus = emptyList(),
          ),
      )

  private val billingScope by lazy { activity.lifecycleScope }

  // Atomic to ensure that multi-thread or unconfined scope callers don't read-mutate race
  private val backoffCount = AtomicInteger(1)

  init {
    Logger.d { "Construct new interactor and billing client" }

    val rawPackageName = activity.applicationContext.packageName
    val packageName =
        rawPackageName.removeSuffix(MarketLinker.DEV_SUFFIX).removeSuffix(MarketLinker.OSS_SUFFIX)

    appSkuList =
        listOf(
            "$packageName.iap_one",
            "$packageName.iap_three",
            "$packageName.iap_five",
            "$packageName.iap_ten",
        )
  }

  protected fun onDisconnected() {
    Logger.w { "Billing client was disconnected!" }

    billingScope.launch(context = dispatchers.default) {
      val waitTime = backoffCount.get()
      val newBackoffCount = backoffCount.updateAndGet { it * BACKOFF_SCALE }

      if (newBackoffCount < BACKOFF_LIMIT) {
        Logger.d { "Wait to reconnect for $waitTime seconds" }
        delay(waitTime.seconds)

        withContext(context = dispatchers.default) {
          Logger.d { "Try connecting again" }
          onClientConnect()
        }
      } else {
        Logger.w { "We have tried to connect and have been unsuccessful. Billing DISABLED" }
      }
    }
  }

  final override suspend fun refresh(): Unit =
      withContext(context = dispatchers.default) { onClientRefresh() }

  final override fun watchSkuList(): Flow<BillingFlowState> = skuFlow

  final override suspend fun purchase(activity: ComponentActivity, sku: BillingSku): Unit =
      withContext(context = dispatchers.default) {
        try {
          onPurchase(activity, sku)
        } catch (@LintIgnoreTooGenericExceptionCaught e: Throwable,) {
          e.ifNotCancellation {
            Logger.e(e) { "Failed purchase flow for SKU: $sku" }
            emitError(RuntimeException(e.message ?: "An error occurred during purchasing."))
          }
        }
      }

  final override fun watchBillingErrors(): Flow<Throwable> = errorBus

  final override fun watchBillingPurchases(): Flow<BillingPurchase> = purchaseBus

  @CheckResult protected fun getSkuList(): List<String> = appSkuList

  protected fun resetBackoff() {
    backoffCount.set(1)
  }

  protected suspend fun emitError(throwable: Throwable) {
    errorBus.emit(throwable)
  }

  protected fun emitStateUpdate(state: BillingFlowState) {
    skuFlow.value = state
  }

  protected suspend fun emitPurchase(purchase: BillingPurchase) {
    purchaseBus.emit(purchase)
  }

  protected fun launchInScope(
      context: CoroutineContext = EmptyCoroutineContext,
      start: CoroutineStart = CoroutineStart.DEFAULT,
      block: suspend CoroutineScope.() -> Unit,
  ): Job = billingScope.launch(context, start, block)

  protected abstract suspend fun onPurchase(activity: ComponentActivity, sku: BillingSku)

  protected abstract suspend fun onClientRefresh()

  public companion object {

    private const val BACKOFF_SCALE = 2
    private const val BACKOFF_LIMIT = 1024
  }
}
