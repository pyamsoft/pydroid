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
import com.pyamsoft.pydroid.tool.Offloader;
import com.pyamsoft.pydroid.tool.SerialOffloader;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

class DonateInteractorImpl implements DonateInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final ICheckout checkout;

  DonateInteractorImpl(@NonNull ICheckout checkout) {
    this.checkout = checkout;
  }

  @Override
  public void bindCallbacks(@NonNull Inventory.Callback callback, @NonNull OnBillingSuccessListener success,
      @NonNull OnBillingErrorListener error) {
    Timber.d("Create checkout purchase flow");
    checkout.init(callback, success, error);
  }

  @Override public void create(@NonNull Activity activity) {
    checkout.createForActivity(activity);
    checkout.start();
  }

  @Override public void destroy() {
    Timber.d("Stop checkout purchase flow");
    checkout.stop();
  }

  @Override public void loadInventory() {
    Timber.d("Load inventory from checkout");
    checkout.loadInventory();
  }

  @Override public void purchase(@NonNull Sku sku) {
    Timber.i("Purchase item: %s", sku.id);
    checkout.purchase(sku);
  }

  @Override public void consume(@NonNull String token) {
    Timber.d("Attempt consume token: %s", token);
    checkout.consume(token);
  }

  @NonNull @Override public Offloader<Boolean> processBillingResult(int requestCode, int resultCode,
      @Nullable Intent data) {
    Timber.i("Process billing onFinish");
    return SerialOffloader.newInstance(
        () -> checkout.processBillingResult(requestCode, resultCode, data));
  }
}
