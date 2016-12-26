/*
 * Copyright 2016 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.pydroid.donate;

import android.app.Activity;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.TestUtils;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.checkout.Inventory;

import static com.pyamsoft.pydroid.TestUtils.getAppCompatActivityController;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 23)
public class DonateInteractorTest {

  @Mock WrappedCheckout mockCheckout;
  private GuardedCheckout guardedCheckout;
  private DonateInteractorImpl interactor;

  private void mockInitializedResponse(boolean init, boolean create) {
    Mockito.when(mockCheckout.isInitialized()).thenReturn(init);
    Mockito.when(mockCheckout.hasCheckout()).thenReturn(create);
  }

  @Before public void setup() {
    mockCheckout = Mockito.mock(WrappedCheckout.class);
    guardedCheckout = new GuardedCheckout(mockCheckout);
    interactor = new DonateInteractorImpl(guardedCheckout);
  }

  @Test public void testInit() {
    mockInitializedResponse(Boolean.FALSE, Boolean.FALSE);

    final Inventory.Callback listener = products -> {
    };

    final DonateInteractor.OnBillingSuccessListener successListener = () -> {
    };

    final DonateInteractor.OnBillingErrorListener errorListener = () -> {
    };

    final AtomicInteger count = new AtomicInteger(0);
    // Mock responses
    Mockito.doAnswer(invocation -> {
      count.incrementAndGet();
      return null;
    }).when(mockCheckout).init(listener, successListener, errorListener);

    // Create should set these three listeners
    interactor.init(listener, successListener, errorListener);
    assertEquals(1, count.get());
  }

  @Test public void testCreateBeforeInitThrows() {
    mockInitializedResponse(Boolean.FALSE, Boolean.FALSE);

    final Activity activity = getAppCompatActivityController().create(null).get();

    // Calling create before init should throw
    boolean thrown;
    try {
      thrown = false;
      interactor.create(activity);
    } catch (IllegalStateException e) {
      TestUtils.expected("Got expected exception: %s", e);
      thrown = true;
    }
    assertTrue(thrown);
  }

  @Test public void testCreateWithInit() {
    testInit();
    mockInitializedResponse(Boolean.TRUE, Boolean.FALSE);

    final Activity activity = getAppCompatActivityController().create(null).get();

    final AtomicInteger count = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      count.incrementAndGet();
      mockInitializedResponse(Boolean.TRUE, Boolean.TRUE);
      return null;
    }).when(mockCheckout).createForActivity(activity);
    Mockito.doAnswer(invocation -> {
      count.incrementAndGet();
      return null;
    }).when(mockCheckout).start();

    interactor.create(activity);
    assertEquals(2, count.get());
  }

  @Test public void testDuplicateCreateThrows() {
    testInit();
    mockInitializedResponse(Boolean.TRUE, Boolean.FALSE);

    final Activity activity = getAppCompatActivityController().create(null).get();
    Mockito.doAnswer(invocation -> {
      mockInitializedResponse(Boolean.TRUE, Boolean.TRUE);
      return null;
    }).when(mockCheckout).createForActivity(activity);

    interactor.create(activity);

    boolean thrown;
    try {
      thrown = false;
      interactor.create(activity);
    } catch (IllegalStateException e) {
      TestUtils.expected("Got expected exception: %s", e);
      thrown = true;
    }

    assertTrue(thrown);
  }

  @Test public void testDestroyCleansUp() {
    testInit();
    mockInitializedResponse(Boolean.TRUE, Boolean.TRUE);

    final AtomicInteger count = new AtomicInteger(0);
    // Mock responses
    Mockito.doAnswer(invocation -> {
      count.incrementAndGet();
      return null;
    }).when(mockCheckout).stop();

    // Destroy should unset listeners and stop purchase flow
    interactor.destroy();
    assertEquals(1, count.get());
  }

  @Test public void testDestroyBeforeInitThrows() {
    mockInitializedResponse(Boolean.FALSE, Boolean.FALSE);

    boolean thrown;
    try {
      thrown = false;
      interactor.destroy();
    } catch (IllegalStateException e) {
      TestUtils.expected("Got expected exception: %s", e);
      thrown = true;
    }

    assertTrue(thrown);
  }

  //@Test public void testLoad() {
  //  // Monitor started state
  //  final AtomicBoolean started = new AtomicBoolean(false);
  //  Mockito.doAnswer(invocation -> {
  //    started.set(true);
  //    return null;
  //  }).when(mockCheckout).start();
  //  Mockito.doAnswer(invocation -> {
  //    started.set(false);
  //    return null;
  //  }).when(mockCheckout).stop();
  //
  //  // Create a load listener
  //  final AtomicBoolean loaded = new AtomicBoolean(false);
  //  final Inventory.Callback listener = products -> {
  //    log("Loaded products!");
  //    loaded.set(true);
  //  };
  //
  //  // Calling load before started should onFinish in an onError
  //  Mockito.doAnswer(invocation -> {
  //    if (!started.get()) {
  //      throw new IllegalStateException("Cannot call loadInventory() before start()");
  //    }
  //    //noinspection ConstantConditions
  //    listener.onLoaded(null);
  //    return null;
  //  }).when(mockCheckout).loadInventory();
  //
  //  // Check for expected onError
  //  assertFalse(started.get());
  //  try {
  //    interactor.loadInventory();
  //  } catch (IllegalStateException e) {
  //    expected("Error in loading: %s", e);
  //  }
  //  assertFalse(started.get());
  //
  //  // Test load flow
  //  assertFalse(started.get());
  //  // We can pass null in for listeners, it should not fail, but API normal usage does not accept it
  //  //noinspection ConstantConditions
  //  interactor.init(listener, null, null);
  //
  //  assertTrue(started.get());
  //
  //  assertFalse(loaded.get());
  //  interactor.loadInventory();
  //  assertTrue(loaded.get());
  //}
  //
  //@Test public void testProcessBillingRequest() {
  //  // Monitor started state
  //  final AtomicBoolean started = new AtomicBoolean(false);
  //  Mockito.doAnswer(invocation -> {
  //    started.set(true);
  //    return null;
  //  }).when(mockCheckout).start();
  //  Mockito.doAnswer(invocation -> {
  //    started.set(false);
  //    return null;
  //  }).when(mockCheckout).stop();
  //
  //  final AtomicBoolean billingSuccess = new AtomicBoolean(false);
  //  final AtomicBoolean shouldThrow = new AtomicBoolean(false);
  //  Mockito.when(mockCheckout.processBillingResult(0, 0, null)).thenAnswer(new Answer<Boolean>() {
  //    @Override public Boolean answer(InvocationOnMock invocation) throws Throwable {
  //      if (shouldThrow.get()) {
  //        throw new RuntimeException("Random Billing exception!");
  //      }
  //
  //      if (started.get()) {
  //        billingSuccess.set(true);
  //      }
  //
  //      return started.get();
  //    }
  //  });
  //
  //  // Make sure that before being created, billing does not process
  //  assertFalse(started.get());
  //  assertFalse(billingSuccess.get());
  //  final Offloader<Boolean> billing = interactor.processBillingResult(0, 0, null)
  //      .onError(item -> expected("Billing exception: %s", item))
  //      .onResult(item -> log("onBillingResult: %s", item));
  //
  //  //noinspection CheckResult
  //  billing.execute();
  //  assertFalse(billingSuccess.get());
  //
  //  // Once created, we should be able to process
  //  //noinspection ConstantConditions
  //  interactor.init(null, null, null);
  //  assertTrue(started.get());
  //
  //  //noinspection CheckResult
  //  billing.execute();
  //  assertTrue(billingSuccess.get());
  //
  //  // Make sure we catch errors in billing process gracefully
  //  shouldThrow.set(true);
  //  //noinspection CheckResult
  //  billing.execute();
  //}
}
