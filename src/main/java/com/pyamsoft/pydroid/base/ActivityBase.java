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
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.support.BugReportDialog;
import com.pyamsoft.pydroid.support.DonationUnavailableDialog;
import com.pyamsoft.pydroid.support.SupportDialog;
import com.pyamsoft.pydroid.util.AnimUtil;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.IMMLeakUtil;
import java.util.List;
import timber.log.Timber;

public abstract class ActivityBase extends AppCompatActivity {

  private static final long BACK_PRESSED_DELAY = 1600L;
  private static final String BUG_REPORT_TAG = "bug_report";
  private static final String SUPPORT_TAG = "support";
  private static final String DONATION_UNAVAILABLE_TAG = "donation_unavailable";

  private boolean backBeenPressed;
  private Handler handler;
  private Toast backBeenPressedToast;
  private Runnable backBeenPressedRunnable;

  @SuppressLint("ShowToast") protected void enableBackBeenPressedConfirmation() {
    backBeenPressed = false;
    handler = new Handler();
    backBeenPressedToast = Toast.makeText(this, "Press Again to Exit", Toast.LENGTH_SHORT);
    backBeenPressedRunnable = () -> backBeenPressed = false;
    handler.removeCallbacksAndMessages(null);
  }

  protected boolean shouldConfirmBackPress() {
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

  protected void setActionBarUpEnabled(final boolean enabled) {
    final ActionBar bar = getSupportActionBar();
    if (bar != null) {
      bar.setHomeButtonEnabled(enabled);
      bar.setDisplayHomeAsUpEnabled(enabled);
    }
  }

  protected void setupFakeFullscreenWindow() {
    getWindow().getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
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

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    final MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_support, menu);
    return true;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    IMMLeakUtil.fixFocusedViewLeak(getApplication());
    super.onCreate(savedInstanceState);
    if (shouldConfirmBackPress()) {
      enableBackBeenPressedConfirmation();
    }
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

  protected void consumeLeftOverPurchases(final BillingProcessor billingProcessor) {
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

  protected abstract String getPlayStoreAppPackage();

  public abstract BillingProcessor getBillingProcessor();

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

