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

package com.pyamsoft.pydroid.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.pyamsoft.pydroid.support.BugReportDialog;
import com.pyamsoft.pydroid.support.DonationUnavailableDialog;
import com.pyamsoft.pydroid.support.SupportDialog;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.IMMLeakUtil;
import java.util.List;
import timber.log.Timber;

public abstract class ActivityBase extends AppCompatActivity
    implements BillingProcessor.IBillingHandler {

  private static final long BACK_PRESSED_DELAY = 1600L;
  private static final String BUG_REPORT_TAG = "bug_report";
  private static final String SUPPORT_TAG = "support";
  private static final String DONATION_UNAVAILABLE_TAG = "donation_unavailable";

  private boolean backBeenPressed;
  private Handler handler;
  private Toast backBeenPressedToast;
  private Runnable backBeenPressedRunnable;
  private BillingProcessor billingProcessor;

  /**
   * Override if you do not want to handle IMM leaks
   */
  protected boolean shouldHandleIMMLeaks() {
    return true;
  }

  /**
   * Override this if you want normal back button behavior
   */
  protected boolean shouldConfirmBackPress() {
    return true;
  }

  /**
   * Override this if the application does not implement IAB donations
   */
  protected boolean isDonationSupported() {
    return true;
  }

  /**
   * Override if you do not want the Window to behave like a fullscreen one
   */
  protected boolean isFakeFullscreen() {
    return false;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
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

  @Override public boolean onCreateOptionsMenu(Menu menu) {
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
      backBeenPressedToast.show();
      handler.postDelayed(backBeenPressedRunnable, BACK_PRESSED_DELAY);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    final int itemId = item.getItemId();
    boolean handled;
    if (itemId == R.id.menu_support) {
      showSupportDialog();
      handled = true;
    } else if (itemId == R.id.menu_bugreport) {
      showBugReportDialog();
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (isDonationSupported()) {
      if (billingProcessor == null) {
        throw new NullPointerException("Donation Supported Application has NULL Billing Processor");
      }
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
      if (billingProcessor == null) {
        throw new NullPointerException("Donation Supported Application has NULL Billing Processor");
      }
      billingProcessor.release();
    }
  }

  @Override public final void onProductPurchased(String productId, TransactionDetails details) {
    Timber.d("onProductPurchased");
    Timber.d("Details: %s", details);
    if (isDonationSupported()) {
      if (billingProcessor == null) {
        throw new NullPointerException("Donation Supported Application has NULL Billing Processor");
      }
      Timber.d("Consume item: %s with token %s", details.productId, details.purchaseToken);
      billingProcessor.consumePurchase(productId);
    } else {
      throw new NullPointerException("Tried to consume purchase with NULL BillingProcessor");
    }
  }

  @Override public final void onPurchaseHistoryRestored() {
    Timber.d("onPurchaseHistoryRestored");
  }

  @Override public final void onBillingError(int errorCode, Throwable error) {
    Timber.e(error, "onBillingError: %d", errorCode);
  }

  @Override public final void onBillingInitialized() {
    Timber.d("onBillingInitialized");
    consumeLeftOverPurchases();
  }

  protected void setActionBarUpEnabled(final boolean enabled) {
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

  private void showBugReportDialog() {
    new BugReportDialog().show(getSupportFragmentManager(), null);
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(), new BugReportDialog(),
        BUG_REPORT_TAG);
  }

  private void showSupportDialog() {
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        SupportDialog.newInstance(getPlayStoreAppPackage()), SUPPORT_TAG);
  }

  protected void showDonationUnavailableDialog() {
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        new DonationUnavailableDialog(), DONATION_UNAVAILABLE_TAG);
  }

  protected final void enableShadows(final View bar, final View shadow) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Lollipop.enableShadows(bar, shadow);
    } else {
      OldAndroid.enableShadows(shadow);
    }
  }

  protected final void disableShadows(final View bar, final View shadow) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Lollipop.disableShadows(bar, shadow);
    } else {
      OldAndroid.disableShadows(shadow);
    }
  }

  protected final void animateActionBarToolbar(final Toolbar toolbar) {
    if (toolbar == null) {
      return;
    }
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

  public final void purchase(final String sku) {
    if (isDonationSupported()) {
      if (billingProcessor == null) {
        throw new NullPointerException("Donation Supported Application has NULL Billing Processor");
      }
      billingProcessor.purchase(this, sku);
    } else {
      Timber.e("Cannot call purchases in a non-donation supported Application");
    }
  }

  protected abstract String getPlayStoreAppPackage();

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) static class Lollipop {

    static void enableShadows(final View t, final View shadow) {
      if (t != null) {
        final float elevation = AppUtil.convertToDP(t.getContext(), 8);
        ViewCompat.setElevation(t, elevation);
      }
      if (shadow != null) {
        shadow.setVisibility(View.GONE);
      }
    }

    static void disableShadows(final View t, final View shadow) {
      if (t != null) {
        t.setElevation(0);
      }
      if (shadow != null) {
        shadow.setVisibility(View.GONE);
      }
    }
  }

  static class OldAndroid {

    static void enableShadows(final View shadow) {
      if (shadow != null) {
        shadow.setVisibility(View.VISIBLE);
      }
    }

    static void disableShadows(final View shadow) {
      if (shadow != null) {
        shadow.setVisibility(View.GONE);
      }
    }
  }
}

