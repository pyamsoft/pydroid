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

package com.pyamsoft.pydroid.ads;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.app.activity.BackPressConfirmActivity;

public abstract class AdvertisementActivity extends BackPressConfirmActivity {

  @Nullable private AdvertisementView adView;

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final int adViewResId = bindActivityToView();
    if (adViewResId != 0) {
      adView = (AdvertisementView) findViewById(adViewResId);
    }

    if (adView != null) {
      adView.create(provideOnlineAdSource(), savedInstanceState);
    }
  }

  @SuppressWarnings({ "WeakerAccess", "SameReturnValue" }) @CheckResult @Nullable
  protected AdSource provideOnlineAdSource() {
    return null;
  }

  @CallSuper @Override protected void onStart() {
    super.onStart();
    if (adView != null) {
      adView.start();
    }
  }

  @Override protected void onStop() {
    super.onStop();
    if (adView != null) {
      adView.stop();
    }
  }

  @CallSuper @Override protected void onDestroy() {
    super.onDestroy();
    if (adView != null) {
      adView.destroy(isChangingConfigurations());
    }
  }

  public final void showAd() {
    if (adView != null) {
      adView.showAd();
    }
  }

  public final void hideAd() {
    if (adView != null) {
      adView.hideAd();
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    if (adView != null) {
      adView.saveState(outState);
    }
    super.onSaveInstanceState(outState);
  }

  /**
   * Call setContentView here and return the id of the advertisement view, 0 if none
   */
  @CheckResult protected abstract int bindActivityToView();
}

