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
import com.pyamsoft.pydroid.billing.BillingPurchase.Fake
import com.pyamsoft.pydroid.billing.fake.FakeBillingInteractor
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.bus.EventBus.Companion
import com.pyamsoft.pydroid.util.AppDispatchers
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
    // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
    minSdk = Build.VERSION_CODES.O,
    maxSdk = Build.VERSION_CODES.BAKLAVA,
)
public class FakeBillingInteractorTest {

  private fun newConnected(
      activity: ComponentActivity,
      errorBus: EventBus<Throwable> = Companion.create(),
      purchaseBus: EventBus<BillingPurchase> = Companion.create(),
      // TODO(Peter): Do we need test control over dispatchers here?
      dispatchers: AppDispatchers = AppDispatchers.create(),
  ) =
      FakeBillingInteractor(
              errorBus = errorBus,
              purchaseBus = purchaseBus,
              dispatchers = dispatchers,
          )
          .bind(activity)

  @Test
  public fun refresh_emitsFakeSkuList(): TestResult = runTest {
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val connected = newConnected(activity)

    connected.refresh()

    val snapshot = connected.watchSkuList().first { it.status == BillingState.CONNECTED }
    assertEquals(12, snapshot.skus.size)
  }

  @Test
  public fun purchase_cheapSku_emitsSuccessfulPurchase(): TestResult = runTest {
    val purchaseBus = EventBus.create<BillingPurchase>()
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val connected = newConnected(activity, purchaseBus = purchaseBus)

    connected.refresh()
    val cheapSku =
        connected.watchSkuList().first { it.status == BillingState.CONNECTED }.skus.first()

    val received = async { purchaseBus.first() }
    purchaseBus.subscriptionCount.first { it == 1 }
    connected.purchase(activity, cheapSku)

    assertIs<Fake>(received.await())
  }

  @Test
  public fun purchase_expensiveSku_emitsError(): TestResult = runTest {
    val errorBus = EventBus.create<Throwable>()
    val activity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    val connected = newConnected(activity, errorBus = errorBus)

    connected.refresh()
    val expensiveSku =
        connected.watchSkuList().first { it.status == BillingState.CONNECTED }.skus.last()

    val received = async { errorBus.first() }
    errorBus.subscriptionCount.first { it == 1 }
    connected.purchase(activity, expensiveSku)

    received.await()
  }
}
