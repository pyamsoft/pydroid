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

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.ResponseCodes;
import org.solovyev.android.checkout.Sku;
import timber.log.Timber;

/**
 * The real checkout module, uses android-checkout to talk with Google Play's InAppBilling services
 */
class RealCheckout implements ICheckout {

  @SuppressWarnings("WeakerAccess") @NonNull final ActivityCheckout checkout;
  @SuppressWarnings("WeakerAccess") @Nullable Inventory.Listener inventoryListener;
  @SuppressWarnings("WeakerAccess") @Nullable SupportInteractor.OnBillingSuccessListener
      successListener;
  @SuppressWarnings("WeakerAccess") @Nullable SupportInteractor.OnBillingErrorListener
      errorListener;
  @Nullable private Inventory inventory;

  RealCheckout(@NonNull ActivityCheckout checkout) {
    this.checkout = checkout;
  }

  @Override public void setInventoryListener(@Nullable Inventory.Listener inventoryListener) {
    this.inventoryListener = inventoryListener;
  }

  @Override public void setSuccessListener(
      @Nullable SupportInteractor.OnBillingSuccessListener successListener) {
    this.successListener = successListener;
  }

  @Override
  public void setErrorListener(@Nullable SupportInteractor.OnBillingErrorListener errorListener) {
    this.errorListener = errorListener;
  }

  @Override public void start() {
    checkout.start();
  }

  @Override public void createPurchaseFlow() {
    checkout.createPurchaseFlow(new DonationPurchaseListener());
  }

  @Override public void loadInventory() {
    if (inventory == null) {
      inventory = checkout.loadInventory();
    }

    if (inventoryListener != null) {
      inventory.load().whenLoaded(inventoryListener);
    }
  }

  @Override public void stop() {
    checkout.stop();
    successListener = null;
    errorListener = null;
    inventoryListener = null;
    inventory = null;
  }

  @Override public void purchase(@NonNull Sku sku) {
    checkout.whenReady(new Checkout.ListenerAdapter() {
      @Override public void onReady(@NonNull BillingRequests requests) {
        requests.purchase(sku, null, checkout.getPurchaseFlow());
      }
    });
  }

  @Override public void consume(@NonNull String token) {
    checkout.whenReady(new Checkout.ListenerAdapter() {
      @Override public void onReady(@NonNull BillingRequests requests) {
        requests.consume(token, new ConsumeListener());
      }
    });
  }

  @Override
  public void processBillingResult(int requestCode, int resultCode, @Nullable Intent data) {
    checkout.onActivityResult(requestCode, resultCode, data);
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
