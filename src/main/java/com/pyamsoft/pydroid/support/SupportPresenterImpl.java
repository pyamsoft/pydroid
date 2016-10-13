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
import com.pyamsoft.pydroid.presenter.PresenterBase;
import org.solovyev.android.checkout.Inventory;

class SupportPresenterImpl extends PresenterBase<SupportPresenter.View>
    implements SupportPresenter, Inventory.Listener {

  @NonNull private final SupportInteractor interactor;
  @NonNull private final SupportInteractor.OnBillingSuccessListener successListener;
  @NonNull private final SupportInteractor.OnBillingErrorListener errorListener;

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
  }

  @Override public void loadInventory() {
    interactor.loadInventory();
  }

  @Override public void onDonationResult(int requestCode, int resultCode, @Nullable Intent data) {
    interactor.processBillingResult(requestCode, resultCode, data);
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
