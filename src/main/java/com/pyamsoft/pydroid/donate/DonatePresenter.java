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

package com.pyamsoft.pydroid.donate;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.presenter.Presenter;
import org.solovyev.android.checkout.Inventory;

public interface DonatePresenter extends Presenter<DonatePresenter.View> {

  void create(@NonNull Activity activity);

  void loadInventory();

  void onBillingResult(int requestCode, int resultCode, @Nullable Intent data);

  void checkoutInAppPurchaseItem(@NonNull SkuModel skuModel);

  interface View {

    void onBillingSuccess();

    void onBillingError();

    void onProcessResultSuccess();

    void onProcessResultError();

    void onProcessResultFailed();

    void onInventoryLoaded(@NonNull Inventory.Products products);
  }
}
