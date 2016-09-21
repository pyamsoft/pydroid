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

package com.pyamsoft.pydroid;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;

public abstract class AdvertisementActivity extends BackPressConfirmActivity {

  @Nullable private AdvertisementView adView;

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final int adViewResId = bindActivityToView();
    if (adViewResId != 0) {
      adView = (AdvertisementView) findViewById(adViewResId);
    }

    if (adView != null) {
      adView.create();
    }
  }

  @CallSuper @Override protected void onStart() {
    if (adView != null) {
      adView.start();
    }
    super.onStart();
  }

  @Override protected void onStop() {
    if (adView != null) {
      adView.stop();
    }
    super.onStop();
  }

  @CallSuper @Override protected void onDestroy() {
    if (adView != null) {
      adView.destroy();
    }
    super.onDestroy();
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

  /**
   * Call setContentView here and return the id of the advertisement view, 0 if none
   */
  @CheckResult protected abstract int bindActivityToView();
}

