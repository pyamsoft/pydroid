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
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.pydroid.presenter.PresenterBase;
import com.pyamsoft.pydroid.tool.ExecutedOffloader;
import com.pyamsoft.pydroid.tool.OffloaderHelper;
import org.solovyev.android.checkout.Inventory;
import timber.log.Timber;

class SupportPresenterImpl extends PresenterBase<SupportPresenter.View>
    implements SupportPresenter, Inventory.Callback {

  @SuppressWarnings("WeakerAccess") @NonNull @VisibleForTesting
  final SupportInteractor.OnBillingSuccessListener successListener;
  @SuppressWarnings("WeakerAccess") @NonNull @VisibleForTesting
  final SupportInteractor.OnBillingErrorListener errorListener;
  @NonNull private final SupportInteractor interactor;
  @NonNull private ExecutedOffloader billingResult = new ExecutedOffloader.Empty();

  SupportPresenterImpl(@NonNull SupportInteractor interactor) {
    this.interactor = interactor;
    successListener = () -> getView(View::onBillingSuccess);
    errorListener = () -> getView(View::onBillingError);
  }

  @Override protected void onBind() {
    super.onBind();
    interactor.create(this, successListener, errorListener);
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    interactor.destroy();
    OffloaderHelper.cancel(billingResult);
  }

  @Override public void loadInventory() {
    interactor.loadInventory();
  }

  @Override public void onBillingResult(int requestCode, int resultCode, @Nullable Intent data) {
    OffloaderHelper.cancel(billingResult);
    billingResult =
        interactor.processBillingResult(requestCode, resultCode, data).onError(throwable -> {
          Timber.e(throwable, "Error processing Billing onFinish");
          getView(View::onProcessResultError);
        }).onResult(result -> getView(view -> {
          if (result) {
            view.onProcessResultSuccess();
          } else {
            view.onProcessResultFailed();
          }
        })).execute();
  }

  @Override public void checkoutInAppPurchaseItem(@NonNull SkuUIItem skuUIItem) {
    if (skuUIItem.isPurchased()) {
      interactor.consume(skuUIItem.getToken());
    } else {
      interactor.purchase(skuUIItem.getSku());
    }
  }

  @Override public void onLoaded(@NonNull Inventory.Products products) {
    getView(view -> view.onInventoryLoaded(products));
  }
}
