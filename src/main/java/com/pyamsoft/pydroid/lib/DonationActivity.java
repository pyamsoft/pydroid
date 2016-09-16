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
import com.pyamsoft.pydroid.support.DonationUnavailableDialog;
import com.pyamsoft.pydroid.util.AppUtil;
import java.util.Arrays;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Products;

public abstract class DonationActivity extends VersionCheckActivity {

  @NonNull public static final String IN_APP_PRODUCT_ID = "IN_APP";
  @NonNull private static final String SKU_DONATE_ONE = ".donate.one";
  @NonNull private static final String SKU_DONATE_TWO = ".donate.two";
  @NonNull private static final String SKU_DONATE_FIVE = ".donate.five";
  @NonNull private static final String SKU_DONATE_TEN = ".donate.ten";
  @NonNull private static final String DONATION_UNAVAILABLE_TAG = "donation_unavailable";
  @NonNull private static final String SUPPORT_TAG = "SupportDialog";

  private String APP_SKU_DONATE_ONE;
  private String APP_SKU_DONATE_TWO;
  private String APP_SKU_DONATE_FIVE;
  private String APP_SKU_DONATE_TEN;
  private ActivityCheckout checkout;

  private void showDonationUnavailableDialog() {
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        new DonationUnavailableDialog(), DONATION_UNAVAILABLE_TAG);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final String packageName = getPackageName();
    APP_SKU_DONATE_ONE = packageName + SKU_DONATE_ONE;
    APP_SKU_DONATE_TWO = packageName + SKU_DONATE_TWO;
    APP_SKU_DONATE_FIVE = packageName + SKU_DONATE_FIVE;
    APP_SKU_DONATE_TEN = packageName + SKU_DONATE_TEN;

    checkout = Checkout.forActivity(this,
        new Billing(getApplicationContext(), new Billing.DefaultConfiguration() {
          @NonNull @Override public String getPublicKey() {
            return getApplication().getPackageName();
          }
        }), Products.create()
            .add(IN_APP_PRODUCT_ID,
                Arrays.asList(APP_SKU_DONATE_ONE, APP_SKU_DONATE_TWO, APP_SKU_DONATE_FIVE,
                    APP_SKU_DONATE_TEN)));

    checkout.start();
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
}
