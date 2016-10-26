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

package com.pyamsoft.pydroid;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.about.AboutLibrariesModule;
import com.pyamsoft.pydroid.about.LicenseProvider;
import com.pyamsoft.pydroid.ads.AdvertisementModule;
import com.pyamsoft.pydroid.social.SocialMediaModule;
import com.pyamsoft.pydroid.support.SupportModule;
import com.pyamsoft.pydroid.version.ApiModule;
import com.pyamsoft.pydroid.version.VersionCheckModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.PurchaseVerifier;
import org.solovyev.android.checkout.RequestListener;

public class PYDroidModule {

  @NonNull private final Provider provider;

  PYDroidModule(@NonNull Context context, @NonNull LicenseProvider licenseProvider) {
    provider = new Provider(context, licenseProvider);
  }

  // Create a new one every time
  @CheckResult @NonNull final AboutLibrariesModule provideAboutLibrariesModule() {
    return new AboutLibrariesModule(provider);
  }

  // Create a new one every time
  @CheckResult @NonNull final SupportModule provideSupportModule(@NonNull Activity activity) {
    return new SupportModule(provider, activity);
  }

  // Create a new one every time
  @CheckResult @NonNull final SocialMediaModule provideSocialMediaModule() {
    return new SocialMediaModule(provider);
  }

  // Create a new one every time
  @CheckResult @NonNull final VersionCheckModule provideVersionCheckModule() {
    return new VersionCheckModule(provider, new ApiModule());
  }

  // Create a new one every time
  //
  // NOTE: Makes a new SocialMediaModule
  @CheckResult @NonNull final AdvertisementModule provideAdvertisementModule() {
    return new AdvertisementModule(provider, provideSocialMediaModule());
  }

  public static class Provider {

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

    Provider(final @NonNull Context context, @NonNull LicenseProvider licenseProvider) {
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

    @CheckResult @NonNull List<String> createInAppPurchaseList(@NonNull Context context) {
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

      if (BuildConfig.DEBUG) {
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

    @SuppressWarnings("WeakerAccess") static class DonationBillingConfiguration
        extends Billing.DefaultConfiguration {

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
    }

    @SuppressWarnings("WeakerAccess") static class AlwaysPurchaseVerifier
        implements PurchaseVerifier {

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

        final List<Purchase> verifiedPurchases = new ArrayList<>(purchases.size());
        for (Purchase purchase : purchases) {
          verifiedPurchases.add(purchase);
        }
        listener.onSuccess(verifiedPurchases);
      }
    }
  }
}
