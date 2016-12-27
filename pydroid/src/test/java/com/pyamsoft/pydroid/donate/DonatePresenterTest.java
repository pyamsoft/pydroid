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

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.BuildConfig;
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

  private CountDownLatch latch;
  @Mock DonateInteractor mockInteractor;
  private DonatePresenterImpl presenter;

  @Before public void setup() {
    mockInteractor = Mockito.mock(DonateInteractor.class);
    presenter = new DonatePresenterImpl(mockInteractor);
    latch = new CountDownLatch(1);
  }

  /**
   * Binding the presenter should also bind callbacks in the interactor
   */
  @Test public void testBind() throws InterruptedException {
    Mockito.doAnswer(invocation -> {
      latch.countDown();
      return null;
    })
        .when(mockInteractor)
        .bindCallbacks(presenter, presenter.getSuccessListener(), presenter.getErrorListener());

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

    if (!latch.await(5, TimeUnit.SECONDS)) {
      throw new RuntimeException("Latch did not count down within 5 seconds");
    }
  }
}
