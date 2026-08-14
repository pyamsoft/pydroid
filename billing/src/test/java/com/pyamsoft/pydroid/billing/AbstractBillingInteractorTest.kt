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
import com.pyamsoft.pydroid.billing.RecordingConnectedBillingInteractor.State
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.util.AppDispatchers
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertSame
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Tracks if it has been connected by a test activitiy component */
private class RecordingConnectedBillingInteractor :
    ConnectedBillingInteractor by ConnectedBillingInteractor.NO_OP {

  enum class State {
    NONE,
    CONNECTED,
    DISCONNECTED,
  }

  val state = AtomicReference<State>(State.NONE)

  override suspend fun onClientConnect() {
    state.set(State.CONNECTED)
  }

  override fun onClientDisconnect() {
    state.set(State.DISCONNECTED)
  }
}

/** Simple no-op test interactor */
private class TestBillingInteractor(
    private val connected: RecordingConnectedBillingInteractor,
    dispatchers: AppDispatchers,
) :
    AbstractBillingInteractor(
        errorBus = EventBus.create(),
        purchaseBus = EventBus.create(),
        dispatchers = dispatchers,
    ) {

  override fun connect(
      activity: ComponentActivity,
      errorBus: EventBus<Throwable>,
      purchaseBus: EventBus<BillingPurchase>,
  ): ConnectedBillingInteractor = connected
}

@RunWith(RobolectricTestRunner::class)
@Config(
    // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
    minSdk = Build.VERSION_CODES.O,
    maxSdk = Build.VERSION_CODES.BAKLAVA,
)
public class AbstractBillingInteractorTest {

  private fun awaitTrue(
      state: AtomicReference<State>,
      awaitState: State,
      timeout: Duration,
  ) {
    val deadline = System.currentTimeMillis() + timeout.inWholeMilliseconds
    var myState = state.get()
    while (System.currentTimeMillis() < deadline) {
      if (myState == awaitState) {
        return
      }

      // Otherwise just wait more
      Thread.sleep(100)
      myState = state.get()
    }

    throw AssertionError(
        "Condition was not met within $timeout: expect=${awaitState} actual=$myState"
    )
  }

  @Test
  public fun bind_fullFlow() {
    val connected = RecordingConnectedBillingInteractor()
    val interactor =
        TestBillingInteractor(
            connected = connected,
            // TODO(Peter): Do we need test control over dispatchers?
            dispatchers = AppDispatchers.create(),
        )
    val controller = Robolectric.buildActivity(ComponentActivity::class.java)
    val activity = controller.get()

    val result = interactor.bind(activity)
    assertSame(connected, result)

    // Starts empty
    assertSame(connected.state.get(), State.NONE)

    // Trigger create event
    controller.create()
    awaitTrue(connected.state, State.CONNECTED, 5.seconds)

    // Trigger destroy event
    controller.destroy()
    awaitTrue(connected.state, State.DISCONNECTED, 5.seconds)
  }
}
