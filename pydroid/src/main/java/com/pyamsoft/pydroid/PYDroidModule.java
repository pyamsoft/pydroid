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

package com.pyamsoft.pydroid;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.about.LicenseProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.PurchaseVerifier;
import org.solovyev.android.checkout.RequestListener;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class PYDroidModule {

  @NonNull private static final String SKU_DONATE = ".donate";
  @NonNull private static final String SKU_DONATE_ONE = SKU_DONATE + ".one";
  @NonNull private static final String SKU_DONATE_TWO = SKU_DONATE + ".two";
  @NonNull private static final String SKU_DONATE_FIVE = SKU_DONATE + ".five";
  @NonNull private static final String SKU_DONATE_TEN = SKU_DONATE + ".ten";

  // Singleton
  @NonNull private final Context appContext;
  @NonNull private final LicenseProvider licenseProvider;
  @NonNull private final Billing billing;
  @NonNull private final List<String> inAppPurchaseList;

  public PYDroidModule(@NonNull Context context, @NonNull LicenseProvider licenseProvider) {
    //noinspection ConstantConditions
    if (context == null) {
      throw new NullPointerException("Application cannot be NULL");
    }

    appContext = context.getApplicationContext();
    this.licenseProvider = licenseProvider;
    inAppPurchaseList = createInAppPurchaseList(context);
    billing =
        new Billing(appContext, new DonationBillingConfiguration(appContext.getPackageName()));
  }

  @CheckResult @NonNull private List<String> createInAppPurchaseList(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
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

    if (BuildConfigChecker.getInstance().isDebugMode()) {
      skuList.add("android.test.purchased");
      skuList.add("android.test.canceled");
      skuList.add("android.test.refunded");
      skuList.add("android.test.item_unavailable");
    }

    return Collections.unmodifiableList(skuList);
  }

  // Singleton
  @CheckResult @NonNull public final Context provideContext() {
    return appContext;
  }

  @CheckResult @NonNull public final PYDroidPreferences providePreferences() {
    return PYDroidPreferences.Instance.getInstance(provideContext());
  }

  // Singleton
  @CheckResult @NonNull public final Billing provideBilling() {
    return billing;
  }

  // Singleton
  @CheckResult @NonNull public final LicenseProvider provideLicenseProvider() {
    return licenseProvider;
  }

  // Singleton
  @CheckResult @NonNull public final List<String> provideInAppPurchaseList() {
    return inAppPurchaseList;
  }

  // Singleton
  @CheckResult @NonNull public final Scheduler provideSubScheduler() {
    return Schedulers.io();
  }

  // Singleton
  @CheckResult @NonNull public final Scheduler provideObsScheduler() {
    return AndroidSchedulers.mainThread();
  }

  private static class DonationBillingConfiguration extends Billing.DefaultConfiguration {

    @NonNull private final String publicKey;

    DonationBillingConfiguration(@NonNull String publicKey) {
      //noinspection ConstantConditions
      if (publicKey == null) {
        throw new NullPointerException("Public Key cannot be NULL");
      }
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

    private static class AlwaysPurchaseVerifier implements PurchaseVerifier {

      /**
       * Verify all purchases as 'valid'
       */
      @Override public void verify(@NonNull List<Purchase> purchases,
          @NonNull RequestListener<List<Purchase>> listener) {
        //noinspection ConstantConditions
        if (purchases == null) {
          throw new NullPointerException("Purchases cannot be NULL");
        }

        //noinspection ConstantConditions
        if (listener == null) {
          throw new NullPointerException("Listener cannot be NULL");
        }

        listener.onSuccess(purchases);
      }
    }
  }
}
