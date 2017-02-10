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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Sku;
import rx.Observable;
import timber.log.Timber;

@RestrictTo(RestrictTo.Scope.LIBRARY) class DonateInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final ICheckout checkout;

  DonateInteractor(@NonNull ICheckout checkout) {
    this.checkout = checkout;
  }

  void bindCallbacks(@NonNull Inventory.Callback callback,
      @NonNull OnBillingSuccessListener success, @NonNull OnBillingErrorListener error) {
    checkout.init(callback, success, error);
  }

  void create(@NonNull Activity activity) {
    checkout.createForActivity(activity);
    checkout.start();
  }

  void destroy() {
    checkout.stop();
  }

  void loadInventory() {
    checkout.loadInventory();
  }

  void purchase(@NonNull Sku sku) {
    Timber.i("Purchase item: %s", sku.id);
    checkout.purchase(sku);
  }

  void consume(@NonNull String token) {
    Timber.d("Attempt consume token: %s", token);
    checkout.consume(token);
  }

  @CheckResult @NonNull Observable<Boolean> processBillingResult(int requestCode, int resultCode,
      @Nullable Intent data) {
    return Observable.fromCallable(
        () -> checkout.processBillingResult(requestCode, resultCode, data));
  }

  interface OnBillingErrorListener {

    void onBillingError();
  }

  interface OnBillingSuccessListener {

    void onBillingSuccess();
  }
}
