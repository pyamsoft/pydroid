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
import android.content.Intent;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.TestUtils;
import com.pyamsoft.pydroid.tool.SerialOffloader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.checkout.Inventory;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class, sdk = 23)
public class DonatePresenterTest {

  @Mock DonateInteractor mockInteractor;
  private CountDownLatch singleLatch;
  private DonatePresenterImpl presenter;

  @Before public void setup() {
    mockInteractor = Mockito.mock(DonateInteractor.class);
    presenter = new DonatePresenterImpl(mockInteractor);
    singleLatch = new CountDownLatch(1);
  }

  /**
   * Binding the presenter should also bind callbacks in the interactor
   */
  @Test public void testBind() throws InterruptedException {
    Mockito.doAnswer(invocation -> {
      singleLatch.countDown();
      return null;
    })
        .when(mockInteractor)
        .bindCallbacks(presenter, presenter.getSuccessListener(), presenter.getErrorListener());

    // Test that binding presenter also binds interactor callbacks
    presenter.bindView(new DonatePresenter.View() {
      @Override public void onBillingSuccess() {

      }

      @Override public void onBillingError() {

      }

      @Override public void onProcessResultSuccess() {

      }

      @Override public void onProcessResultError() {

      }

      @Override public void onProcessResultFailed() {

      }

      @Override public void onInventoryLoaded(@NonNull Inventory.Products products) {

      }
    });

    if (!singleLatch.await(5, TimeUnit.SECONDS)) {
      throw new RuntimeException("Latch did not count down within 5 seconds");
    }
  }

  @Test public void testUnbind() throws InterruptedException {
    Mockito.doAnswer(invocation -> {
      singleLatch.countDown();
      return null;
    }).when(mockInteractor).destroy();

    // Test that unbinding presenter clears interactor
    presenter.unbindView();

    if (!singleLatch.await(5, TimeUnit.SECONDS)) {
      throw new RuntimeException("Latch did not count down within 5 seconds");
    }
  }

  @Test public void testCreateLoadsInventory() {
    final Activity activity = TestUtils.getAppCompatActivityController().create().get();

    final AtomicInteger counter = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      counter.incrementAndGet();
      return null;
    }).when(mockInteractor).create(activity);

    Mockito.doAnswer(invocation -> {
      counter.incrementAndGet();
      return null;
    }).when(mockInteractor).loadInventory();

    assertEquals(0, counter.get());
    presenter.create(activity);
    assertEquals(2, counter.get());
  }

  @Test public void testLoadsInventory() throws InterruptedException {
    Mockito.doAnswer(invocation -> {
      singleLatch.countDown();
      return null;
    }).when(mockInteractor).loadInventory();

    presenter.loadInventory();
    if (!singleLatch.await(5, TimeUnit.SECONDS)) {
      throw new RuntimeException("Latch did not count down within 5 seconds");
    }
  }

  @Test public void testProcessBillingResultFailureUnbound() {
    final Intent dataIntent = new Intent();

    // Billing error
    Mockito.when(mockInteractor.processBillingResult(0, 0, dataIntent))
        .thenReturn(SerialOffloader.newInstance(() -> Boolean.FALSE));

    presenter.onBillingResult(0, 0, dataIntent);
  }

  @Test public void testProcessBillingResultSuccessUnbound() {
    final Intent dataIntent = new Intent();

    // Billing success
    Mockito.when(mockInteractor.processBillingResult(1, 1, dataIntent))
        .thenReturn(SerialOffloader.newInstance(() -> Boolean.TRUE));

    presenter.onBillingResult(1, 1, dataIntent);
  }

  @Test public void testProcessBillingResultErrorUnbound() {
    final Intent dataIntent = new Intent();

    // Billing throws error
    Mockito.when(mockInteractor.processBillingResult(2, 2, dataIntent))
        .thenReturn(SerialOffloader.newInstance(() -> {
          throw new RuntimeException();
        }));

    presenter.onBillingResult(2, 2, dataIntent);
  }

  @Test public void testProcessBillingResultSuccess() throws InterruptedException {
    final Intent dataIntent = new Intent();

    // Billing success
    Mockito.when(mockInteractor.processBillingResult(0, 0, dataIntent))
        .thenReturn(SerialOffloader.newInstance(() -> Boolean.TRUE));

    presenter.bindView(new DonatePresenter.View() {
      @Override public void onBillingSuccess() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onBillingError() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onProcessResultSuccess() {
        singleLatch.countDown();
      }

      @Override public void onProcessResultError() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onProcessResultFailed() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onInventoryLoaded(@NonNull Inventory.Products products) {
        throw new RuntimeException("Error should not occur");
      }
    });

    presenter.onBillingResult(0, 0, dataIntent);
    if (!singleLatch.await(5, TimeUnit.SECONDS)) {
      throw new RuntimeException("Latch did not count down within 5 seconds");
    }
  }

  @Test public void testProcessBillingResultFailure() throws InterruptedException {
    final Intent dataIntent = new Intent();

    // Billing success
    Mockito.when(mockInteractor.processBillingResult(0, 0, dataIntent))
        .thenReturn(SerialOffloader.newInstance(() -> Boolean.FALSE));

    presenter.bindView(new DonatePresenter.View() {
      @Override public void onBillingSuccess() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onBillingError() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onProcessResultSuccess() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onProcessResultError() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onProcessResultFailed() {
        singleLatch.countDown();
      }

      @Override public void onInventoryLoaded(@NonNull Inventory.Products products) {
        throw new RuntimeException("Error should not occur");
      }
    });

    presenter.onBillingResult(0, 0, dataIntent);
    if (!singleLatch.await(5, TimeUnit.SECONDS)) {
      throw new RuntimeException("Latch did not count down within 5 seconds");
    }
  }

  @Test public void testProcessBillingResultError() throws InterruptedException {
    final Intent dataIntent = new Intent();

    // Billing success
    Mockito.when(mockInteractor.processBillingResult(0, 0, dataIntent))
        .thenReturn(SerialOffloader.newInstance(() -> {
          throw new RuntimeException();
        }));

    presenter.bindView(new DonatePresenter.View() {
      @Override public void onBillingSuccess() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onBillingError() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onProcessResultSuccess() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onProcessResultError() {
        singleLatch.countDown();
      }

      @Override public void onProcessResultFailed() {
        throw new RuntimeException("Error should not occur");
      }

      @Override public void onInventoryLoaded(@NonNull Inventory.Products products) {
        throw new RuntimeException("Error should not occur");
      }
    });

    presenter.onBillingResult(0, 0, dataIntent);
    if (!singleLatch.await(5, TimeUnit.SECONDS)) {
      throw new RuntimeException("Latch did not count down within 5 seconds");
    }
  }
}
