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

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.pyamsoft.pydroid.R;
import com.pyamsoft.pydroid.support.AdvertisementView;
import com.pyamsoft.pydroid.support.SupportDialog;
import com.pyamsoft.pydroid.util.AppUtil;

public abstract class AdvertisementActivity extends BackPressConfirmActivity {

  @NonNull private static final String SUPPORT_TAG = "SupportDialog";
  @Nullable private AdvertisementView adView;

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final int adViewResId = bindActivityToView();
    if (adViewResId != 0) {
      adView = (AdvertisementView) findViewById(adViewResId);
    }

    if (adView != null) {
      adView.create(provideAdViewResId(), provideAdViewUnitId(), isAdDebugMode());
    }
  }

  @CallSuper @Override protected void onStart() {
    super.onStart();
    showAd();
  }

  @CallSuper @Override protected void onResume() {
    super.onResume();
    if (adView != null) {
      adView.resume();
    }
  }

  @Override protected void onPause() {
    super.onPause();
    if (adView != null) {
      adView.pause();
    }
  }

  @CallSuper @Override protected void onDestroy() {
    super.onDestroy();
    if (adView != null) {
      adView.destroy();
    }
  }

  @CallSuper @Override public boolean onCreateOptionsMenu(@NonNull Menu menu) {
    super.onCreateOptionsMenu(menu);
    final MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_support, menu);
    return true;
  }

  @CallSuper @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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

  private void showSupportDialog() {
    AppUtil.guaranteeSingleDialogFragment(getSupportFragmentManager(),
        SupportDialog.newInstance(getPackageName()), SUPPORT_TAG);
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

  @CheckResult @LayoutRes protected abstract int provideAdViewResId();

  @CheckResult @NonNull protected abstract String provideAdViewUnitId();

  @CheckResult protected abstract boolean isAdDebugMode();
}

