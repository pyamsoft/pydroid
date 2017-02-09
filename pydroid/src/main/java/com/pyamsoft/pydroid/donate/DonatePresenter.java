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
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.tool.ExecutedOffloader;
import com.pyamsoft.pydroid.tool.OffloaderHelper;
import org.solovyev.android.checkout.Inventory;
import timber.log.Timber;

public class DonatePresenter extends Presenter<DonatePresenter.View> {

  @NonNull private final DonateInteractor interactor;
  @SuppressWarnings("WeakerAccess") @Nullable ExecutedOffloader billingResult;

  DonatePresenter(@NonNull DonateInteractor interactor) {
    this.interactor = interactor;
  }

  @Override protected void onBind(@Nullable View view) {
    super.onBind(view);
    interactor.bindCallbacks(products -> {
      Timber.d("Products are loaded");
      ifViewExists(view1 -> view1.onInventoryLoaded(products));
    }, () -> ifViewExists(View::onBillingSuccess), () -> ifViewExists(View::onBillingError));
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    interactor.destroy();
    OffloaderHelper.cancel(billingResult);
  }

  public void create(@NonNull Activity activity) {
    interactor.create(activity);
  }

  public void loadInventory() {
    interactor.loadInventory();
  }

  public void onBillingResult(int requestCode, int resultCode, @Nullable Intent data,
      @NonNull BillingResultCallback callback) {
    OffloaderHelper.cancel(billingResult);
    billingResult =
        interactor.processBillingResult(requestCode, resultCode, data).onError(throwable -> {
          Timber.e(throwable, "Error processing Billing onFinish");
          callback.onProcessResultError();
        }).onResult(success -> {
          if (success) {
            callback.onProcessResultSuccess();
          } else {
            callback.onProcessResultFailed();
          }
        }).onFinish(() -> OffloaderHelper.cancel(billingResult)).execute();
  }

  public void checkoutInAppPurchaseItem(@NonNull SkuModel skuModel) {
    final String token = skuModel.token();
    if (token != null) {
      interactor.consume(token);
    } else {
      interactor.purchase(skuModel.sku());
    }
  }

  public interface BillingResultCallback {

    void onProcessResultSuccess();

    void onProcessResultError();

    void onProcessResultFailed();
  }

  public interface View {

    void onBillingSuccess();

    void onBillingError();

    void onInventoryLoaded(@NonNull Inventory.Products products);
  }
}
