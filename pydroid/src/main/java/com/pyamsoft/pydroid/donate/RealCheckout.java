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

package com.pyamsoft.pydroid.donate;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.ResponseCodes;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

/**
 * The real checkout module, uses android-checkout to talk with Google Play's InAppBilling services
 */
class RealCheckout implements ICheckout {

  @NonNull private final List<String> inAppSkuList;
  @NonNull private final Billing billing;
  @SuppressWarnings("WeakerAccess") @Nullable ActivityCheckout checkout;
  @SuppressWarnings("WeakerAccess") @Nullable Inventory.Callback inventoryCallback;
  @SuppressWarnings("WeakerAccess") @Nullable DonateInteractor.OnBillingSuccessListener
      successListener;
  @SuppressWarnings("WeakerAccess") @Nullable DonateInteractor.OnBillingErrorListener errorListener;
  @Nullable private Inventory inventory;

  RealCheckout(@NonNull Billing billing, @NonNull List<String> inAppSkuList) {
    this.billing = billing;
    this.inAppSkuList = Collections.unmodifiableList(inAppSkuList);
  }

  private void checkCheckoutNonNull() {
    if (checkout == null) {
      throw new IllegalStateException("Checkout is NULL, must create it first");
    }
  }

  private void makeInventory() {
    checkCheckoutNonNull();

    if (inventory == null) {
      //noinspection ConstantConditions
      inventory = checkout.makeInventory();
    }
  }

  @Override public void createForActivity(@NonNull Activity activity) {
    if (checkout != null) {
      throw new IllegalStateException("Checkout is already created for a different Activity");
    }

    checkout = Checkout.forActivity(activity, billing);
  }

  @Override public void init(@NonNull Inventory.Callback inventoryCallback,
      @NonNull DonateInteractor.OnBillingSuccessListener successListener,
      @NonNull DonateInteractor.OnBillingErrorListener errorListener) {
    this.inventoryCallback = inventoryCallback;
    this.successListener = successListener;
    this.errorListener = errorListener;
  }

  @Override public void start() {
    checkCheckoutNonNull();

    //noinspection ConstantConditions
    checkout.start();
    checkout.createPurchaseFlow(new DonationPurchaseListener());
    makeInventory();
  }

  @Override public void loadInventory() {
    checkCheckoutNonNull();
    makeInventory();

    if (inventoryCallback != null) {
      //noinspection ConstantConditions
      inventory.load(
          Inventory.Request.create().loadAllPurchases().loadSkus(ProductTypes.IN_APP, inAppSkuList),
          inventoryCallback);
    }
  }

  @Override public void stop() {
    checkCheckoutNonNull();

    //noinspection ConstantConditions
    checkout.destroyPurchaseFlow();
    checkout.stop();
    inventory = null;
    inventoryCallback = null;
    successListener = null;
    errorListener = null;
    checkout = null;
  }

  @Override public void purchase(@NonNull Sku sku) {
    checkCheckoutNonNull();

    //noinspection ConstantConditions
    checkout.whenReady(new Checkout.EmptyListener() {
      @Override public void onReady(@NonNull BillingRequests requests) {
        super.onReady(requests);
        checkCheckoutNonNull();

        //noinspection ConstantConditions
        requests.purchase(sku, null, checkout.getPurchaseFlow());
      }
    });
  }

  @Override public void consume(@NonNull String token) {
    checkCheckoutNonNull();

    //noinspection ConstantConditions
    checkout.whenReady(new Checkout.EmptyListener() {
      @Override public void onReady(@NonNull BillingRequests requests) {
        super.onReady(requests);
        requests.consume(token, new ConsumeListener());
      }
    });
  }

  @Override
  public boolean processBillingResult(int requestCode, int resultCode, @Nullable Intent data) {
    checkCheckoutNonNull();

    //noinspection ConstantConditions
    return checkout.onActivityResult(requestCode, resultCode, data);
  }

  abstract class BaseRequestListener<T> implements RequestListener<T> {

    void processResult() {
      loadInventory();
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
