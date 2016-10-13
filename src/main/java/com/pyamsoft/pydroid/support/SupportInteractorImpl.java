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
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.BuildConfig;
import java.util.ArrayList;
import java.util.List;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Products;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.PurchaseVerifier;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.ResponseCodes;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

class SupportInteractorImpl implements SupportInteractor {

  @SuppressWarnings("WeakerAccess") @NonNull final ActivityCheckout checkout;
  @SuppressWarnings("WeakerAccess") @Nullable OnBillingSuccessListener successListener;
  @SuppressWarnings("WeakerAccess") @Nullable OnBillingErrorListener errorListener;
  @SuppressWarnings("WeakerAccess") @Nullable Inventory.Listener inventoryListener;
  @Nullable private Inventory inAppPurchaseInventory;

  SupportInteractorImpl(@NonNull Activity activity) {
    checkout = CheckoutFactory.create(activity);
    checkout.start();
  }

  @SuppressWarnings("WeakerAccess") @NonNull @CheckResult Inventory getInAppPurchaseInventory() {
    if (inAppPurchaseInventory == null) {
      throw new NullPointerException("InAppPurchaseInventory is NULL");
    }
    return inAppPurchaseInventory;
  }

  @Override public void create(@NonNull Inventory.Listener listener,
      @NonNull OnBillingSuccessListener success, @NonNull OnBillingErrorListener error) {
    this.successListener = success;
    this.errorListener = error;

    Timber.d("Create checkout purchase flow");
    checkout.createPurchaseFlow(new DonationPurchaseListener());
    inAppPurchaseInventory = checkout.loadInventory();
    inventoryListener = listener;
  }

  @Override public void destroy() {
    Timber.d("Stop checkout purchase flow");
    checkout.stop();
    successListener = null;
    errorListener = null;
    inventoryListener = null;
  }

  @Override public void loadInventory() {
    if (inventoryListener != null) {
      checkout.loadInventory().load().whenLoaded(inventoryListener);
    }
  }

  @Override public void purchase(@NonNull Sku sku) {
    Timber.i("Purchase item: %s", sku.id);
    checkout.whenReady(new Checkout.ListenerAdapter() {
      @Override public void onReady(@NonNull BillingRequests requests) {
        requests.purchase(sku, null, checkout.getPurchaseFlow());
      }
    });
  }

  @Override public void consume(@NonNull String token) {
    Timber.d("Attempt consume token: %s", token);
    checkout.whenReady(new Checkout.ListenerAdapter() {
      @Override public void onReady(@NonNull BillingRequests requests) {
        requests.consume(token, new ConsumeListener());
      }
    });
  }

  @Override
  public void processBillingResult(int requestCode, int resultCode, @Nullable Intent data) {
    Timber.i("Process billing result");
    checkout.onActivityResult(requestCode, resultCode, data);
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

    @CheckResult @NonNull static ActivityCheckout create(@NonNull Activity activity) {
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

      return Checkout.forActivity(activity,
          new Billing(appContext, new DonationBillingConfiguration(packageName)),
          Products.create().add(ProductTypes.IN_APP, skuList));
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

  abstract class BaseRequestListener<T> implements RequestListener<T> {

    void processResult() {
      if (inventoryListener != null) {
        getInAppPurchaseInventory().load().whenLoaded(inventoryListener);
      }
    }

    @CallSuper @Override public void onError(int response, @NonNull Exception e) {
      Timber.e(e, "Billing Error");
      processResult();
    }
  }

  @SuppressWarnings("WeakerAccess") class DonationPurchaseListener
      extends BaseRequestListener<Purchase> {

    DonationPurchaseListener() {
    }

    @Override public void onSuccess(@NonNull Purchase result) {
      Timber.d("Purchase successful, attempt to consume: %s", result.sku);
      consume(result.token);
    }

    @Override public void onError(int response, @NonNull Exception e) {
      if (response != ResponseCodes.USER_CANCELED) {
        if (errorListener != null) {
          errorListener.onBillingError();
        }
      } else {
        super.onError(response, e);
      }
    }
  }

  @SuppressWarnings("WeakerAccess") class ConsumeListener extends BaseRequestListener<Object> {

    @Override public void onSuccess(@NonNull Object result) {
      processResult();
      if (successListener != null) {
        successListener.onBillingSuccess();
      }
    }

    @Override public void onError(int response, @NonNull Exception e) {
      super.onError(response, e);
      // it is possible that our data is not synchronized with data on Google Play => need to handle some errors
      if (errorListener != null) {
        errorListener.onBillingError();
      }
    }
  }
}
