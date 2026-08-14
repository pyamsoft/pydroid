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

import android.os.Build
import androidx.activity.ComponentActivity
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.util.AppDispatchers
import com.pyamsoft.pydroid.util.MarketLinker
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private data class TestSku(
    override val id: String = "sku",
    override val displayPrice: String = "$1.00",
    override val price: Long = 100,
    override val title: String = "Test SKU",
    override val description: String = "A test sku",
) : BillingSku

private class TestConnectedBillingInteractor(
    activity: ComponentActivity,
    errorBus: EventBus<Throwable>,
    purchaseBus: EventBus<BillingPurchase>,
    dispatchers: AppDispatchers,
    private val onPurchaseAction: suspend () -> Unit = {},
) :
    AbstractConnectedBillingInteractor(
        activity = activity,
        errorBus = errorBus,
        dispatchers = dispatchers,
        purchaseBus = purchaseBus,
    ) {

  val refreshCount = AtomicInteger(0)

  override suspend fun onClientConnect() = Unit

  override fun onClientDisconnect() = Unit

  override suspend fun onPurchase(activity: ComponentActivity, sku: BillingSku) {
    onPurchaseAction()
  }

  override suspend fun onClientRefresh() {
    refreshCount.incrementAndGet()
  }

  // Alias method to expose an otherwise "protected" function
  fun testEmitStateUpdate(state: BillingFlowState) = emitStateUpdate(state)

  // Alias method to expose an otherwise "protected" function
  fun testGetSkuList() = getSkuList()
}

@RunWith(RobolectricTestRunner::class)
@Config(
    // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
    minSdk = Build.VERSION_CODES.O,
    maxSdk = Build.VERSION_CODES.BAKLAVA,
)
public class AbstractConnectedBillingInteractorTest {

  private fun newInteractor(
      activity: ComponentActivity,
      errorBus: EventBus<Throwable> = EventBus.create(),
      purchaseBus: EventBus<BillingPurchase> = EventBus.create(),
      // TODO(Peter): Do we need test control over dispatchers here?
      dispatchers: AppDispatchers = AppDispatchers.create(),
      onPurchaseAction: suspend () -> Unit = {},
  ) =
      TestConnectedBillingInteractor(
          activity = activity,
          errorBus = errorBus,
          purchaseBus = purchaseBus,
          dispatchers = dispatchers,
          onPurchaseAction = onPurchaseAction,
      )

  @Test
  public fun refresh_delegatesToOnClientRefresh(): TestResult = runTest {
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val interactor = newInteractor(activity)

    interactor.refresh()

    assertEquals(1, interactor.refreshCount.get())
  }

  @Test
  public fun watchSkuList_reflectsEmittedState(): TestResult = runTest {
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val interactor = newInteractor(activity)
    val sku = TestSku()

    interactor.testEmitStateUpdate(
        BillingFlowState(
            state = BillingState.CONNECTED,
            list = listOf(sku),
        ),
    )

    val snapshot = interactor.watchSkuList().first()
    assertEquals(BillingState.CONNECTED, snapshot.status)
    assertEquals(listOf(sku), snapshot.skus)
  }

  @Test
  public fun purchase_success_invokesOnPurchaseWithoutError(): TestResult = runTest {
    val invoked = AtomicInteger(0)
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val interactor = newInteractor(activity, onPurchaseAction = { invoked.incrementAndGet() })

    interactor.purchase(activity, TestSku())

    assertEquals(1, invoked.get())
  }

  @Test
  public fun purchase_onPurchaseThrows_emitsWrappedError(): TestResult = runTest {
    val errorBus = EventBus.create<Throwable>()
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val interactor =
        newInteractor(
            activity,
            errorBus = errorBus,
            onPurchaseAction = { throw IllegalStateException("boom") },
        )

    val received = async { errorBus.first() }
    errorBus.subscriptionCount.first { it == 1 }
    interactor.purchase(activity, TestSku())

    assertEquals("boom", received.await().message)
  }

  @Test
  public fun purchase_onPurchaseCancels_propagatesInsteadOfSwallowing(): TestResult = runTest {
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val interactor =
        newInteractor(activity, onPurchaseAction = { throw CancellationException("cancelled") })

    assertFailsWith<CancellationException> { interactor.purchase(activity, TestSku()) }
  }

  @Test
  public fun watchBillingErrors_reflectsErrorBus(): TestResult = runTest {
    val errorBus = EventBus.create<Throwable>()
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val interactor = newInteractor(activity, errorBus = errorBus)

    val received = async { interactor.watchBillingErrors().first() }
    errorBus.subscriptionCount.first { it == 1 }
    errorBus.emit(RuntimeException("kaboom"))

    assertEquals("kaboom", received.await().message)
  }

  @Test
  public fun watchBillingPurchases_reflectsPurchaseBus(): TestResult = runTest {
    val purchaseBus = EventBus.create<BillingPurchase>()
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val interactor = newInteractor(activity, purchaseBus = purchaseBus)
    val purchase = BillingPurchase.Fake(TestSku())

    val received = async { interactor.watchBillingPurchases().first() }
    purchaseBus.subscriptionCount.first { it == 1 }
    purchaseBus.emit(purchase)

    assertEquals(purchase, received.await())
  }

  @Test
  public fun getSkuList_derivesFromApplicationPackageName() {
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val interactor = newInteractor(activity)
    val packageName = activity.applicationContext.packageName.removeSuffix(MarketLinker.DEV_SUFFIX)

    val skuList = interactor.testGetSkuList()

    assertEquals(
        listOf(
            "$packageName.iap_one",
            "$packageName.iap_three",
            "$packageName.iap_five",
            "$packageName.iap_ten",
        ),
        skuList,
    )
  }
}
