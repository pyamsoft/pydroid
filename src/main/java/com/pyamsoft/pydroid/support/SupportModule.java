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
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.PYDroidModule;
import java.util.ArrayList;
import java.util.List;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.PurchaseVerifier;
import org.solovyev.android.checkout.RequestListener;

public class SupportModule {

  @NonNull private final SupportInteractor interactor;
  @NonNull private final SupportPresenter presenter;

  public SupportModule(@NonNull PYDroidModule.Provider pyDroidModule, @NonNull Activity activity) {
    interactor = new SupportInteractorImpl(CheckoutFactory.create(activity));
    presenter = new SupportPresenterImpl(interactor);
  }

  @NonNull @CheckResult public SupportPresenter getPresenter() {
    return presenter;
  }

  @SuppressWarnings("WeakerAccess") static final class CheckoutFactory {
    @NonNull private static final String SKU_DONATE = ".donate";
    @NonNull private static final String SKU_DONATE_ONE = SKU_DONATE + ".one";
    @NonNull private static final String SKU_DONATE_TWO = SKU_DONATE + ".two";
    @NonNull private static final String SKU_DONATE_FIVE = SKU_DONATE + ".five";
    @NonNull private static final String SKU_DONATE_TEN = SKU_DONATE + ".ten";

    private CheckoutFactory() {
      throw new RuntimeException("No instances");
    }

    @CheckResult @NonNull static RealCheckout create(@NonNull Activity activity) {
      final Context appContext = activity.getApplicationContext();
      final String packageName = appContext.getPackageName();
      final String appSpecificSkuDonateOne = packageName + SKU_DONATE_ONE;
      final String appSpecificSkuDonateTwo = packageName + SKU_DONATE_TWO;
      final String appSpecificSkuDonateFive = packageName + SKU_DONATE_FIVE;
      final String appSpecificSkuDonateTen = packageName + SKU_DONATE_TEN;

      final List<String> skuList = new ArrayList<>();
      skuList.add(appSpecificSkuDonateOne);
      skuList.add(appSpecificSkuDonateTwo);
      skuList.add(appSpecificSkuDonateFive);
      skuList.add(appSpecificSkuDonateTen);

      if (BuildConfig.DEBUG) {
        skuList.add("android.test.purchased");
        skuList.add("android.test.canceled");
        skuList.add("android.test.refunded");
        skuList.add("android.test.item_unavailable");
      }

      final ActivityCheckout checkout = Checkout.forActivity(activity,
          new Billing(appContext, new DonationBillingConfiguration(packageName)));
      return new RealCheckout(checkout, skuList);
    }
  }

  @SuppressWarnings("WeakerAccess") static class DonationBillingConfiguration
      extends Billing.DefaultConfiguration {

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
  }

  @SuppressWarnings("WeakerAccess") static class AlwaysPurchaseVerifier
      implements PurchaseVerifier {

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
