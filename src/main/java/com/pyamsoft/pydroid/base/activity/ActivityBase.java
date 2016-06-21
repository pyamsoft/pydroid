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

package com.pyamsoft.pydroid.base.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.support.DonationUnavailableDialog;
import com.pyamsoft.pydroid.support.SupportDialog;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.IMMLeakUtil;
import java.util.List;
import timber.log.Timber;

abstract class ActivityBase extends AppCompatActivity implements BillingProcessor.IBillingHandler {

  private static final long BACK_PRESSED_DELAY = 1600L;
  @NonNull private static final String SUPPORT_TAG = "support";
  @NonNull private static final String DONATION_UNAVAILABLE_TAG = "donation_unavailable";

  boolean backBeenPressed;
  private Handler handler;
  private Toast backBeenPressedToast;
  private Runnable backBeenPressedRunnable;
  private BillingProcessor billingProcessor;

  /**
   * Override if you do not want to handle IMM leaks
   */
  @CheckResult protected boolean shouldHandleIMMLeaks() {
    return true;
  }

  /**
   * Override this if you want normal back button behavior
   */
  @CheckResult protected boolean shouldConfirmBackPress() {
    return true;
  }

  /**
   * Override this if the application does not implement IAB donations
   */
  @CheckResult protected boolean isDonationSupported() {
    return true;
  }

  /**
   * Override if you do not want the Window to behave like a fullscreen one
   */
  @CheckResult protected boolean isFakeFullscreen() {
    return false;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    // These must go before the call to onCreate
    if (shouldHandleIMMLeaks()) {
      IMMLeakUtil.fixFocusedViewLeak(getApplication());
    }
    if (isFakeFullscreen()) {
      setupFakeFullscreenWindow();
    }

    super.onCreate(savedInstanceState);
    if (shouldConfirmBackPress()) {
      enableBackBeenPressedConfirmation();
    }

    if (isDonationSupported()) {
      billingProcessor =
          new BillingProcessor(getApplicationContext(), getPlayStoreAppPackage(), this);
    }
  }

  @Override public boolean onCreateOptionsMenu(@NonNull Menu menu) {
    final MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_support, menu);
    return true;
  }

  @Override public void onBackPressed() {
    if (backBeenPressed || !shouldConfirmBackPress()) {
      backBeenPressed = false;
      if (handler != null) {
        handler.removeCallbacksAndMessages(null);
      }
      super.onBackPressed();
    } else {
      backBeenPressed = true;
      if (backBeenPressedToast != null) {
        backBeenPressedToast.show();
      }
      if (handler != null && backBeenPressedRunnable != null) {
        handler.postDelayed(backBeenPressedRunnable, BACK_PRESSED_DELAY);
      }
    }
  }

  @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    final int itemId = item.getItemId();
    boolean handled;
    if (itemId == R.id.menu_support) {
      showSupportDialog();
      if (!BillingProcessor.isIabServiceAvailable(this)) {
        showDonationUnavailableDialog();
      }
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
    if (isDonationSupported()) {
      if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
        super.onActivityResult(requestCode, resultCode, data);
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (isDonationSupported()) {
      billingProcessor.release();
    }
  }

  @Override public final void onProductPurchased(@NonNull String productId,
      @NonNull TransactionDetails details) {
    Timber.d("onProductPurchased");
    Timber.d("Details: %s", details);
    if (isDonationSupported()) {
      Timber.d("Consume item: %s with token %s", details.productId, details.purchaseToken);
      billingProcessor.consumePurchase(productId);
    } else {
      throw new NullPointerException("Tried to consume purchase with NULL BillingProcessor");
    }
  }

  @Override public final void onPurchaseHistoryRestored() {
    Timber.d("onPurchaseHistoryRestored");
  }

  @Override public final void onBillingError(int errorCode, @NonNull Throwable error) {
    Timber.e(error, "onBillingError: %d", errorCode);
  }

  @Override public final void onBillingInitialized() {
    Timber.d("onBillingInitialized");
    consumeLeftOverPurchases();
  }

  public void setActionBarUpEnabled(final boolean enabled) {
    final ActionBar bar = getSupportActionBar();
    if (bar != null) {
      bar.setHomeButtonEnabled(enabled);
      bar.setDisplayHomeAsUpEnabled(enabled);
    }
  }

  @SuppressLint("ShowToast") private void enableBackBeenPressedConfirmation() {
    backBeenPressed = false;
    handler = new Handler();
    backBeenPressedToast = Toast.makeText(this, "Press Again to Exit", Toast.LENGTH_SHORT);
    backBeenPressedRunnable = () -> backBeenPressed = false;
    handler.removeCallbacksAndMessages(null);
  }

  private void setupFakeFullscreenWindow() {
    getWindow().getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
  }

  private void consumeLeftOverPurchases() {
    if (billingProcessor == null) {
      Timber.e("Billing processor is NULL");
      return;
    }

    final boolean loaded = billingProcessor.loadOwnedPurchasesFromGoogle();
    if (loaded) {
      final List<String> ownedProducts = billingProcessor.listOwnedProducts();
      final int size = ownedProducts.size();
      for (int i = 0; i < size; ++i) {
        final String product = ownedProducts.get(i);
        Timber.d("User owns productId: %s consume it", product);
        if (!billingProcessor.consumePurchase(product)) {
          Timber.e("Could not consume purchase: %s", product);
        }
      }
    }
  }

  private void showSupportDialog() {
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        SupportDialog.newInstance(getPlayStoreAppPackage()), SUPPORT_TAG);
  }

  public final void showDonationUnavailableDialog() {
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        new DonationUnavailableDialog(), DONATION_UNAVAILABLE_TAG);
  }

  protected final void enableShadows(final @NonNull View bar, final @NonNull View shadow) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Lollipop.enableShadows(bar, shadow);
    } else {
      OldAndroid.enableShadows(shadow);
    }
  }

  protected final void disableShadows(final @NonNull View bar, final @NonNull View shadow) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Lollipop.disableShadows(bar, shadow);
    } else {
      OldAndroid.disableShadows(shadow);
    }
  }

  protected final void animateActionBarToolbar(final @NonNull Toolbar toolbar) {
    final View t = toolbar.getChildAt(0);
    if (t != null && t instanceof TextView &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      final TextView title = (TextView) t;
      AnimUtil.fadeIn(title).start();
    }

    final View amv = toolbar.getChildAt(1);
    if (amv != null && amv instanceof ActionMenuView) {
      final ActionMenuView actions = (ActionMenuView) amv;
      final int childCount = actions.getChildCount();
      final int duration = 200;
      int delay = 500;
      for (int i = 0; i < childCount; ++i) {
        final View item = actions.getChildAt(i);
        if (item == null) {
          continue;
        }
        AnimUtil.popShow(item, delay, duration).start();
        delay += duration;
      }
    }
  }

  public final void purchase(final @NonNull String sku) {
    if (isDonationSupported()) {
      billingProcessor.purchase(this, sku);
    } else {
      Timber.e("Cannot call purchases in a non-donation supported Application");
    }
  }

  @CheckResult @NonNull protected abstract String getPlayStoreAppPackage();

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) static class Lollipop {

    static void enableShadows(final @NonNull View t, final @NonNull View shadow) {
      final float elevation = AppUtil.convertToDP(t.getContext(), 8);
      ViewCompat.setElevation(t, elevation);
      shadow.setVisibility(View.GONE);
    }

    static void disableShadows(final @NonNull View t, final @NonNull View shadow) {
      t.setElevation(0);
      shadow.setVisibility(View.GONE);
    }
  }

  static class OldAndroid {

    static void enableShadows(final @NonNull View shadow) {
      shadow.setVisibility(View.VISIBLE);
    }

    static void disableShadows(final @NonNull View shadow) {
      shadow.setVisibility(View.GONE);
    }
  }
}

