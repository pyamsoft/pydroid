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

package com.pyamsoft.pydroid.ui.donate;

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
import com.pyamsoft.pydroid.donate.DonatePresenter;
import com.pyamsoft.pydroid.ui.PYDroidInjector;
import com.pyamsoft.pydroid.ui.R;
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

public abstract class DonationActivity extends VersionCheckActivity
    implements DonatePresenter.View {

  @SuppressWarnings("WeakerAccess") DonatePresenter donatePresenter;

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PYDroidInjector.get().provideComponent().provideDonateComponent().inject(this);
    donatePresenter.bindView(this);
    donatePresenter.create(this);
  }

  @CallSuper @Override protected void onDestroy() {
    super.onDestroy();
    donatePresenter.unbindView();
  }

  /**
   * onActivityResult is always called after onStart, so we will always be bound
   */
  @CallSuper @Override protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    donatePresenter.onBillingResult(requestCode, resultCode, data,
        new DonatePresenter.BillingResultCallback() {
          @Override public void onProcessResultSuccess() {
            passToSupportDialog(DonateDialog::onProcessResultSuccess, null);
          }

          @Override public void onProcessResultError() {
            passToSupportDialog(DonateDialog::onProcessResultError,
                DonationActivity.this::onBillingError);
          }

          @Override public void onProcessResultFailed() {
            passToSupportDialog(DonateDialog::onProcessResultFailed,
                DonationActivity.this::onBillingError);
          }
        });
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
      DonateDialog.show(this);
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  @CheckResult @NonNull DonatePresenter getDonatePresenter() {
    if (donatePresenter == null) {
      throw new IllegalStateException("SupportPresenter is NULL");
    }
    return donatePresenter;
  }

  private void passToSupportDialog(@NonNull ActionSingle<DonateDialog> actionWithDialog,
      @Nullable ActionNone actionWithoutDialog) {
    final Fragment fragment = getSupportFragmentManager().findFragmentByTag(DonateDialog.TAG);
    if (fragment instanceof DonateDialog && fragment.isVisible()) {
      actionWithDialog.call((DonateDialog) fragment);
    } else {
      if (actionWithoutDialog != null) {
        actionWithoutDialog.call();
      }
    }
  }

  @Override public final void onBillingSuccess() {
    passToSupportDialog(DonateDialog::onBillingSuccess,
        () -> Toast.makeText(getApplicationContext(), R.string.purchase_success_msg,
            Toast.LENGTH_SHORT).show());
  }

  @Override public final void onBillingError() {
    passToSupportDialog(DonateDialog::onBillingError,
        () -> Toast.makeText(getApplicationContext(), R.string.purchase_error_msg,
            Toast.LENGTH_SHORT).show());
  }

  @Override public final void onInventoryLoaded(@NonNull Inventory.Products products) {
    final Inventory.Product product = products.get(ProductTypes.IN_APP);
    if (product.supported) {
      Timber.i("IAP Billing is supported");
      // Only reveal non-consumable items
      for (Sku sku : product.getSkus()) {
        Timber.d("Add sku: %s", sku.id);
        final Purchase purchase = product.getPurchaseInState(sku, Purchase.State.PURCHASED);
        if (purchase != null) {
          Timber.i("Item is purchased already, attempt to auto-consume it.");
          final SkuUIItem item = new SkuUIItem(sku, purchase.token);
          getDonatePresenter().checkoutInAppPurchaseItem(item.getModel());
        }
      }
    }

    passToSupportDialog(view -> view.onInventoryLoaded(products), null);
  }
}
