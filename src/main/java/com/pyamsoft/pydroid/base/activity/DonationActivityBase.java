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

package com.pyamsoft.pydroid.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import java.util.List;
import timber.log.Timber;

public abstract class DonationActivityBase extends ActivityBase
    implements BillingProcessor.IBillingHandler {

  private BillingProcessor billingProcessor;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billingProcessor =
        new BillingProcessor(getApplicationContext(), getPlayStoreAppPackage(), this);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
    if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    billingProcessor.release();
  }

  @Override public final void onProductPurchased(@NonNull String productId,
      @NonNull TransactionDetails details) {
    Timber.d("onProductPurchased");
    Timber.d("Details: %s", details);
    Timber.d("Consume item: %s with token %s", details.productId, details.purchaseToken);
    billingProcessor.consumePurchase(productId);
  }

  @Override public final void onPurchaseHistoryRestored() {
    Timber.d("onPurchaseHistoryRestored");
  }

  @Override public final void onBillingError(int errorCode, @NonNull Throwable error) {
    Timber.e(error, "onBillingError: %d", errorCode);
  }

  @Override public final void onBillingInitialized() {
    Timber.d("onBillingInitialized");
    consumeLeftOverPurchases();
  }

  private void consumeLeftOverPurchases() {
    if (billingProcessor == null) {
      Timber.e("Billing processor is NULL");
      return;
    }

    final boolean loaded = billingProcessor.loadOwnedPurchasesFromGoogle();
    if (loaded) {
      final List<String> ownedProducts = billingProcessor.listOwnedProducts();
      final int size = ownedProducts.size();
      for (int i = 0; i < size; ++i) {
        final String product = ownedProducts.get(i);
        Timber.d("User owns productId: %s consume it", product);
        if (!billingProcessor.consumePurchase(product)) {
          Timber.e("Could not consume purchase: %s", product);
        }
      }
    }
  }

  public final void purchase(final @NonNull String sku) {
    billingProcessor.purchase(this, sku);
  }
}
