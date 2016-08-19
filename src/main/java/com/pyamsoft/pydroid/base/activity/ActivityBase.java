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
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.support.AdvertisementView;
import com.pyamsoft.pydroid.support.SupportDialog;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.IMMLeakUtil;

public abstract class ActivityBase extends AppCompatActivity {

  private static final long BACK_PRESSED_DELAY = 1600L;
  @NonNull private static final String SUPPORT_TAG = "support";

  boolean backBeenPressed;
  private Handler handler;
  private Toast backBeenPressedToast;
  private Runnable backBeenPressedRunnable;
  @Nullable private AdvertisementView adView;

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

    final int adViewResId = bindActivityToView();
    if (adViewResId != 0) {
      adView = (AdvertisementView) findViewById(adViewResId);
    }

    if (adView != null) {
      adView.create();
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
      handled = true;
    } else {
      handled = false;
    }
    return handled;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (adView != null) {
      adView.destroy();
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

  private void showSupportDialog() {
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        SupportDialog.newInstance(getPackageName()), SUPPORT_TAG);
  }

  @Override protected void onStart() {
    super.onStart();
    showAd();
  }

  public final void showAd() {
    if (adView != null) {
      adView.show(false);
    }
  }

  public final void hideAd() {
    if (adView != null) {
      adView.hide();
    }
  }

  /**
   * Call setContentView here and return the id of the advertisement view, 0 if none
   */
  @CheckResult protected abstract int bindActivityToView();
}

