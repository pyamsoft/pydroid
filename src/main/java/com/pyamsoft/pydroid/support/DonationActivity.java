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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.pyamsoft.pydroid.ActionNone;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.SupportPresenterProvider;
import com.pyamsoft.pydroid.version.VersionCheckActivity;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

public abstract class DonationActivity extends VersionCheckActivity
    implements SupportPresenter.View {

  @SuppressWarnings("WeakerAccess") SupportPresenter supportPresenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Create presenter here, do not persist
    supportPresenter = new DonationSupportPresenterProvider(this).providePresenter();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    supportPresenter.destroy();
  }

  @Override protected void onStart() {
    super.onStart();
    supportPresenter.bindView(this);
  }

  @Override protected void onStop() {
    super.onStop();
    supportPresenter.unbindView();
  }

  @CallSuper @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    supportPresenter.onBillingResult(requestCode, resultCode, data);
  }

  @CallSuper @Override public boolean onCreateOptionsMenu(@NonNull Menu menu) {
    super.onCreateOptionsMenu(menu);
    final MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_support, menu);
    return true;
  }

  @CallSuper @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    final int itemId = item.getItemId();
    boolean handled;
    if (itemId == R.id.menu_support) {
      SupportDialog.show(getSupportFragmentManager());
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  @CheckResult @NonNull SupportPresenter getSupportPresenter() {
    if (supportPresenter == null) {
      throw new IllegalStateException("SupportPresenter is NULL");
    }
    return supportPresenter;
  }

  private void passToSupportDialog(@NonNull ActionSingle<SupportDialog> actionWithDialog,
      @Nullable ActionNone actionWithoutDialog) {
    final Fragment fragment = getSupportFragmentManager().findFragmentByTag(SupportDialog.TAG);
    if (fragment instanceof SupportDialog) {
      actionWithDialog.call((SupportDialog) fragment);
    } else {
      if (actionWithoutDialog != null) {
        actionWithoutDialog.call();
      }
    }
  }

  @Override public void onBillingSuccess() {
    passToSupportDialog(SupportDialog::onBillingSuccess,
        () -> Toast.makeText(getApplicationContext(), R.string.purchase_success_msg,
            Toast.LENGTH_SHORT).show());
  }

  @Override public void onBillingError() {
    passToSupportDialog(SupportDialog::onBillingError,
        () -> Toast.makeText(getApplicationContext(), R.string.purchase_error_msg,
            Toast.LENGTH_SHORT).show());
  }

  @Override public void onProcessResultSuccess() {
    passToSupportDialog(SupportDialog::onProcessResultSuccess, null);
  }

  @Override public void onProcessResultError() {
    passToSupportDialog(SupportDialog::onProcessResultError, this::onBillingError);
  }

  @Override public void onProcessResultFailed() {
    passToSupportDialog(SupportDialog::onProcessResultFailed, this::onBillingError);
  }

  @Override public void onInventoryLoaded(@NonNull Inventory.Products products) {
    final Inventory.Product product = products.get(ProductTypes.IN_APP);
    if (product.supported) {
      Timber.i("IAP Billing is supported");
      // Only reveal non-consumable items
      for (Sku sku : product.getSkus()) {
        Timber.d("Add sku: %s", sku.id);
        final Purchase purchase = product.getPurchaseInState(sku, Purchase.State.PURCHASED);
        if (purchase != null) {
          final SkuUIItem item = new SkuUIItem(sku, purchase.token);
          if (item.isPurchased()) {
            Timber.i("Item is purchased already, attempt to auto-consume it.");
            getSupportPresenter().checkoutInAppPurchaseItem(item);
          }
        }
      }
    }

    passToSupportDialog(view -> view.onInventoryLoaded(products), null);
  }

  static class DonationSupportPresenterProvider extends SupportPresenterProvider {

    @NonNull private final Activity activity;

    DonationSupportPresenterProvider(@NonNull Activity activity) {
      this.activity = activity;
    }

    @NonNull @Override protected Activity provideActivity() {
      return activity;
    }
  }
}
