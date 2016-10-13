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
 */

package com.pyamsoft.pydroid.support;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.BuildConfig;
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
public class SupportPresenterTest {

  @Mock SupportInteractor mockInteractor;
  private SupportPresenterImpl presenter;

  @Before public void setup() {
    mockInteractor = Mockito.mock(SupportInteractor.class);
    presenter = new SupportPresenterImpl(mockInteractor);
  }

  /**
   * Make sure that calling bind will also initialize the interactor
   */
  @Test public void testBindCreate() {
    final AtomicInteger count = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      count.incrementAndGet();
      return null;
    }).when(mockInteractor).create(presenter, presenter.successListener, presenter.errorListener);

    assertEquals(0, count.get());
    presenter.bindView(new SupportPresenter.View() {
      @Override public void onBillingSuccess() {

      }

      @Override public void onBillingError() {

      }

      @Override public void onInventoryLoaded(@NonNull Inventory.Products products) {

      }
    });

    assertEquals(1, count.get());
  }

  /**
   * Make sure that calling unbind will also destroy the interactor
   */
  @Test public void testUnbindDestroy() {
    final AtomicInteger count = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      count.incrementAndGet();
      return null;
    }).when(mockInteractor).destroy();

    assertEquals(0, count.get());
    presenter.bindView(new SupportPresenter.View() {
      @Override public void onBillingSuccess() {

      }

      @Override public void onBillingError() {

      }

      @Override public void onInventoryLoaded(@NonNull Inventory.Products products) {

      }
    });

    // To ensure proper lifecycle, make sure we use the destroy callback in unbind, NOT destroy
    assertEquals(0, count.get());
    presenter.destroy();

    assertEquals(0, count.get());
    presenter.unbindView();
    assertEquals(1, count.get());
  }

  /**
   * Make sure that loading the inventory, when it calls back, will actually load
   */
  @Test public void testLoadInventory() {
    final AtomicInteger count = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      count.incrementAndGet();
      presenter.onLoaded(new Inventory.Products());
      return null;
    }).when(mockInteractor).loadInventory();

    // When created and then inventory load is called, it does the thing
    assertEquals(0, count.get());
    presenter.bindView(new SupportPresenter.View() {
      @Override public void onBillingSuccess() {

      }

      @Override public void onBillingError() {

      }

      @Override public void onInventoryLoaded(@NonNull Inventory.Products products) {
        count.incrementAndGet();
      }
    });

    // To ensure proper lifecycle, make sure we use the destroy callback in unbind, NOT destroy
    assertEquals(0, count.get());
    presenter.loadInventory();

    assertEquals(2, count.get());
  }

  /**
   * Make sure that processing the inventory is sent to the interactor
   */
  @Test public void testProcessResult() {
    final AtomicInteger count1 = new AtomicInteger(0);
    final AtomicInteger count2 = new AtomicInteger(0);
    Mockito.doAnswer(invocation -> {
      count1.incrementAndGet();
      return null;
    }).when(mockInteractor).processBillingResult(0, 0, null);

    final Intent intent = new Intent();
    Mockito.doAnswer(invocation -> {
      count2.incrementAndGet();
      return null;
    }).when(mockInteractor).processBillingResult(0, 0, intent);

    // Donation result with NULL is handled correctly
    assertEquals(0, count1.get());
    assertEquals(0, count2.get());
    presenter.onDonationResult(0, 0, null);
    assertEquals(1, count1.get());
    assertEquals(0, count2.get());


    // Donation result is handled correctly
    assertEquals(1, count1.get());
    assertEquals(0, count2.get());
    presenter.onDonationResult(0, 0, intent);
    assertEquals(1, count1.get());
    assertEquals(1, count2.get());
  }
}
