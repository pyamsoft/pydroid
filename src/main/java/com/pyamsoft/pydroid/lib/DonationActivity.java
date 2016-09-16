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

package com.pyamsoft.pydroid.lib;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.util.AppUtil;
import java.util.Arrays;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Products;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.Sku;

public abstract class DonationActivity extends VersionCheckActivity {

  @NonNull public static final String IN_APP_PRODUCT_ID = "IN_APP";

  @NonNull private static final String SKU_UNLOCK = ".unlock";
  @NonNull private static final String SKU_UNLOCK_ONE = SKU_UNLOCK + ".one";
  @NonNull private static final String SKU_UNLOCK_TWO = SKU_UNLOCK + ".two";
  @NonNull private static final String SKU_UNLOCK_FIVE = SKU_UNLOCK + ".five";
  @NonNull private static final String SKU_UNLOCK_TEN = SKU_UNLOCK + ".ten";

  @NonNull private static final String SKU_DONATE = ".donate";
  @NonNull private static final String SKU_DONATE_ONE = SKU_DONATE + ".one";
  @NonNull private static final String SKU_DONATE_TWO = SKU_DONATE + ".two";
  @NonNull private static final String SKU_DONATE_FIVE = SKU_DONATE + ".five";
  @NonNull private static final String SKU_DONATE_TEN = SKU_DONATE + ".ten";

  @NonNull private static final String SUPPORT_TAG = "SupportDialog";

  boolean canDisableAds;
  private ActivityCheckout checkout;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    canDisableAds = false;

    final String packageName = getPackageName();
    final String appSpecificSkuUnlockOne = packageName + SKU_UNLOCK_ONE;
    final String appSpecificSkuUnlockTwo = packageName + SKU_UNLOCK_TWO;
    final String appSpecificSkuUnlockFive = packageName + SKU_UNLOCK_FIVE;
    final String appSpecificSkuUnlockTen = packageName + SKU_UNLOCK_TEN;
    final String appSpecificSkuDonateOne = packageName + SKU_DONATE_ONE;
    final String appSpecificSkuDonateTwo = packageName + SKU_DONATE_TWO;
    final String appSpecificSkuDonateFive = packageName + SKU_DONATE_FIVE;
    final String appSpecificSkuDonateTen = packageName + SKU_DONATE_TEN;

    checkout = Checkout.forActivity(this,
        new Billing(getApplicationContext(), new Billing.DefaultConfiguration() {
          @NonNull @Override public String getPublicKey() {
            return getApplication().getPackageName();
          }
        }), Products.create()
            .add(IN_APP_PRODUCT_ID, Arrays.asList(appSpecificSkuUnlockOne, appSpecificSkuUnlockTwo,
                appSpecificSkuUnlockFive, appSpecificSkuUnlockTen, appSpecificSkuDonateOne,
                appSpecificSkuDonateTwo, appSpecificSkuDonateFive, appSpecificSkuDonateTen)));

    checkout.start();
    checkout.loadInventory().load().whenLoaded(new InventoryLoadedListener());
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    checkout.stop();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    checkout.onActivityResult(requestCode, resultCode, data);
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
      showSupportDialog();
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  private void showSupportDialog() {
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(), new SupportDialog(),
        SUPPORT_TAG);
  }

  @NonNull @CheckResult final ActivityCheckout getCheckout() {
    if (checkout == null) {
      throw new NullPointerException("ActivityCheckout is NULL");
    }
    return checkout;
  }

  @CheckResult public boolean canDisableAds() {
    return canDisableAds;
  }

  void setCanDisableAds(boolean canDisableAds) {
    this.canDisableAds = canDisableAds;
  }

  class InventoryLoadedListener implements Inventory.Listener {

    @Override public void onLoaded(@NonNull Inventory.Products products) {
      final Inventory.Product product = products.get(DonationActivity.IN_APP_PRODUCT_ID);
      if (product.supported) {
        for (Sku sku : product.getSkus()) {

          // Consumable items don't count for disabling ads
          if (!SkuItem.isConsumable(sku.id)) {
            final Purchase purchase = product.getPurchaseInState(sku, Purchase.State.PURCHASED);
            if (purchase != null) {
              canDisableAds = true;
              break;
            }
          }
        }
      } else {
        canDisableAds = false;
      }
    }
  }
}
