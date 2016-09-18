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
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.pyamsoft.pydroid.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Products;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.PurchaseVerifier;
import org.solovyev.android.checkout.RequestListener;
import timber.log.Timber;

public abstract class DonationActivity extends VersionCheckActivity {

  @NonNull private static final String SKU_DONATE = ".donate";
  @NonNull private static final String SKU_DONATE_ONE = SKU_DONATE + ".one";
  @NonNull private static final String SKU_DONATE_TWO = SKU_DONATE + ".two";
  @NonNull private static final String SKU_DONATE_FIVE = SKU_DONATE + ".five";
  @NonNull private static final String SKU_DONATE_TEN = SKU_DONATE + ".ten";

  private ActivityCheckout checkout;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final String packageName = getPackageName();
    final String appSpecificSkuDonateOne = packageName + SKU_DONATE_ONE;
    final String appSpecificSkuDonateTwo = packageName + SKU_DONATE_TWO;
    final String appSpecificSkuDonateFive = packageName + SKU_DONATE_FIVE;
    final String appSpecificSkuDonateTen = packageName + SKU_DONATE_TEN;

    Timber.d("Set up IAP checkout");
    checkout = Checkout.forActivity(this,
        new Billing(getApplicationContext(), new DonationBillingConfiguration(getPackageName())),
        Products.create()
            .add(ProductTypes.IN_APP,
                Arrays.asList(appSpecificSkuDonateOne, appSpecificSkuDonateTwo,
                    appSpecificSkuDonateFive, appSpecificSkuDonateTen)));

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
      SupportDialog.show(getSupportFragmentManager(), getApplicationIcon());
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  @NonNull @CheckResult final ActivityCheckout getCheckout() {
    if (checkout == null) {
      throw new NullPointerException("ActivityCheckout is NULL");
    }
    return checkout;
  }

  @DrawableRes @CheckResult protected abstract int getApplicationIcon();

  static class DonationBillingConfiguration extends Billing.DefaultConfiguration {

    @NonNull private final String publicKey;

    DonationBillingConfiguration(@NonNull String publicKey) {
      this.publicKey = publicKey;
    }

    @NonNull @Override public String getPublicKey() {
      return publicKey;
    }

    /**
     * We do not really need any purchase verification as they are all just donations anyway.
     * Our public key is not used, so the default verifier fails anyway.
     *
     * Pass a verifier which always passes
     */
    @NonNull @Override public PurchaseVerifier getPurchaseVerifier() {
      return new AlwaysPurchaseVerifier();
    }

    static class AlwaysPurchaseVerifier implements PurchaseVerifier {

      /**
       * Verify all purchases as 'valid'
       */
      @Override public void verify(@NonNull List<Purchase> purchases,
          @NonNull RequestListener<List<Purchase>> listener) {
        final List<Purchase> verifiedPurchases = new ArrayList<>(purchases.size());
        for (Purchase purchase : purchases) {
          verifiedPurchases.add(purchase);
        }
        listener.onSuccess(verifiedPurchases);
      }
    }
  }
}
