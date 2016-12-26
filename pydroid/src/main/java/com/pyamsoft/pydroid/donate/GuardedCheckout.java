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
import android.support.annotation.Nullable;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Sku;

class GuardedCheckout implements ICheckout {

  @NonNull private final WrappedCheckout wrappedCheckout;

  GuardedCheckout(@NonNull WrappedCheckout wrappedCheckout) {
    this.wrappedCheckout = wrappedCheckout;
  }

  private void checkCheckoutNonNull() {
    if (!wrappedCheckout.hasCheckout()) {
      throw new IllegalStateException("Checkout is NULL, must create it first");
    }
  }

  private void checkCheckoutInitialized() {
    if (!wrappedCheckout.isInitialized()) {
      throw new IllegalStateException("Checkout is not initialized");
    }
  }

  @Override public void init(@NonNull Inventory.Callback inventoryCallback,
      @NonNull DonateInteractor.OnBillingSuccessListener successListener,
      @NonNull DonateInteractor.OnBillingErrorListener errorListener) {
    if (wrappedCheckout.isInitialized()) {
      throw new IllegalStateException("Cannot re-initialized Checkout");
    }

    wrappedCheckout.init(inventoryCallback, successListener, errorListener);
  }

  @Override public void createForActivity(@NonNull Activity activity) {
    if (wrappedCheckout.hasCheckout()) {
      throw new IllegalStateException("Cannot create Checkout twice");
    }

    wrappedCheckout.createForActivity(activity);
  }

  @Override public void loadInventory() {
    checkCheckoutNonNull();
    checkCheckoutInitialized();

    wrappedCheckout.loadInventory();
  }

  @Override public void start() {
    checkCheckoutNonNull();
    checkCheckoutInitialized();

    wrappedCheckout.start();
  }

  @Override public void stop() {
    checkCheckoutNonNull();
    checkCheckoutInitialized();

    wrappedCheckout.stop();
  }

  @Override public void purchase(@NonNull Sku sku) {
    checkCheckoutNonNull();
    checkCheckoutInitialized();

    wrappedCheckout.purchase(sku);
  }

  @Override public void consume(@NonNull String token) {
    checkCheckoutNonNull();
    checkCheckoutInitialized();

    wrappedCheckout.consume(token);
  }

  @Override
  public boolean processBillingResult(int requestCode, int resultCode, @Nullable Intent data) {
    checkCheckoutNonNull();
    checkCheckoutInitialized();

    return wrappedCheckout.processBillingResult(requestCode, resultCode, data);
  }
}
