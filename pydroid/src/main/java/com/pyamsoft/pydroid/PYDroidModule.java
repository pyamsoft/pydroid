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
import com.pyamsoft.pydroid.about.AboutLibrariesModule;
import com.pyamsoft.pydroid.about.LicenseProvider;
import com.pyamsoft.pydroid.ads.AdvertisementModule;
import com.pyamsoft.pydroid.donate.DonateModule;
import com.pyamsoft.pydroid.social.SocialMediaModule;
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

  @NonNull private final AboutLibrariesModule aboutLibrariesModule;
  @NonNull private final DonateModule donateModule;
  @NonNull private final SocialMediaModule socialMediaModule;
  @NonNull private final VersionCheckModule versionCheckModule;
  @NonNull private final AdvertisementModule advertisementModule;

  //@NonNull private final Map<String, Object> cachedSingletons = new HashMap<>();
  //@CheckResult @NonNull
  //final <T> T getCachedSingleton(@NonNull String tag, @NonNull FuncNone<T> creator) {
  //  final Object cachedPlainObject = cachedSingletons.get(tag);
  //  T cachedObject;
  //  boolean objectCreated;
  //  if (cachedPlainObject == null) {
  //    cachedObject = creator.call();
  //    objectCreated = true;
  //  } else {
  //    try {
  //      //noinspection unchecked
  //      cachedObject = (T) cachedPlainObject;
  //      objectCreated = false;
  //    } catch (ClassCastException e) {
  //      Timber.e(e, "Cast error in singleton cache!");
  //      cachedObject = creator.call();
  //      objectCreated = true;
  //    }
  //  }
  //
  //  if (objectCreated) {
  //    // Put new entry into map
  //    cachedSingletons.put(tag, cachedObject);
  //  }
  //  return cachedObject;
  //}

  PYDroidModule(@NonNull Context context, @NonNull LicenseProvider licenseProvider) {
    final Provider provider = new Provider(context, licenseProvider);
    aboutLibrariesModule = new AboutLibrariesModule(provider);
    donateModule = new DonateModule(provider);
    socialMediaModule = new SocialMediaModule();
    versionCheckModule = new VersionCheckModule(new ApiModule());
    advertisementModule = new AdvertisementModule(provider);
  }

  // Create a new one every time
  @CheckResult @NonNull public final AboutLibrariesModule provideAboutLibrariesModule() {
    return aboutLibrariesModule;
  }

  // Create a new one every time
  @CheckResult @NonNull public final DonateModule provideDonateModule() {
    return donateModule;
  }

  // Create a new one every time
  @CheckResult @NonNull public final SocialMediaModule provideSocialMediaModule() {
    return socialMediaModule;
  }

  // Create a new one every time
  @CheckResult @NonNull public final VersionCheckModule provideVersionCheckModule() {
    return versionCheckModule;
  }

  // Create a new one every time
  @CheckResult @NonNull public final AdvertisementModule provideAdvertisementModule() {
    return advertisementModule;
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

    Provider(@NonNull Context context, @NonNull LicenseProvider licenseProvider) {
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
      return PYDroidPreferencesImpl.Instance.getInstance(provideContext());
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

    static class DonationBillingConfiguration extends Billing.DefaultConfiguration {

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

    static class AlwaysPurchaseVerifier implements PurchaseVerifier {

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
