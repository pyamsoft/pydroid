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

package com.pyamsoft.pydroid.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.lib.VersionCheckActivity;
import com.pyamsoft.pydroid.support.DonationUnavailableDialog;
import com.pyamsoft.pydroid.util.AppUtil;
import java.util.List;
import timber.log.Timber;

public abstract class DonationActivity extends VersionCheckActivity
    implements BillingProcessor.IBillingHandler {

  @NonNull private static final String DONATION_UNAVAILABLE_TAG = "donation_unavailable";
  private BillingProcessor billingProcessor;

  public void showDonationUnavailableDialog() {
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        new DonationUnavailableDialog(), DONATION_UNAVAILABLE_TAG);
  }

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    billingProcessor = new BillingProcessor(getApplicationContext(), getPackageName(), this);
  }

  @CallSuper @Override
  protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
    if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @CallSuper @Override protected void onDestroy() {
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

  @CallSuper @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    final boolean superHandled = super.onOptionsItemSelected(item);
    final int itemId = item.getItemId();
    boolean handled;
    if (itemId == R.id.menu_support) {
      if (!BillingProcessor.isIabServiceAvailable(this)) {
        showDonationUnavailableDialog();
      }
      handled = true;
    } else {
      handled = false;
    }

    return handled || superHandled;
  }

  public final void purchase(final @NonNull String sku) {
    billingProcessor.purchase(this, sku);
  }
}
