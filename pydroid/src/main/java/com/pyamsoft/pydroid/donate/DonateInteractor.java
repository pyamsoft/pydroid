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
import com.pyamsoft.pydroid.tool.Offloader;
import com.pyamsoft.pydroid.tool.SerialOffloader;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

public class DonateInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final ICheckout checkout;

  DonateInteractor(@NonNull ICheckout checkout) {
    this.checkout = checkout;
  }

  public void bindCallbacks(@NonNull Inventory.Callback callback,
      @NonNull OnBillingSuccessListener success, @NonNull OnBillingErrorListener error) {
    checkout.init(callback, success, error);
  }

  public void create(@NonNull Activity activity) {
    checkout.createForActivity(activity);
    checkout.start();
  }

  public void destroy() {
    checkout.stop();
  }

  public void loadInventory() {
    checkout.loadInventory();
  }

  public void purchase(@NonNull Sku sku) {
    Timber.i("Purchase item: %s", sku.id);
    checkout.purchase(sku);
  }

  public void consume(@NonNull String token) {
    Timber.d("Attempt consume token: %s", token);
    checkout.consume(token);
  }

  @CheckResult @NonNull
  public Offloader<Boolean> processBillingResult(int requestCode, int resultCode,
      @Nullable Intent data) {
    return SerialOffloader.newInstance(
        () -> checkout.processBillingResult(requestCode, resultCode, data));
  }

  public interface OnBillingErrorListener {

    void onBillingError();
  }

  public interface OnBillingSuccessListener {

    void onBillingSuccess();
  }
}
