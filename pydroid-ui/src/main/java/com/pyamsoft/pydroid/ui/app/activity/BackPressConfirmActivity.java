/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.app.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.widget.Toast;

public abstract class BackPressConfirmActivity extends ActivityBase {

  private static final long BACK_PRESSED_DELAY = 1600L;
  @SuppressWarnings("WeakerAccess") boolean backBeenPressed;
  private Handler handler;
  private Toast backBeenPressedToast;
  private Runnable backBeenPressedRunnable;

  /**
   * Override this if you want normal back button behavior
   */
  @CheckResult protected boolean shouldConfirmBackPress() {
    return true;
  }

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (shouldConfirmBackPress()) {
      enableBackBeenPressedConfirmation();
    }
  }

  @CallSuper @Override public void onBackPressed() {
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

  @SuppressLint("ShowToast") private void enableBackBeenPressedConfirmation() {
    backBeenPressed = false;
    handler = new Handler();
    backBeenPressedToast = Toast.makeText(this, "Press Again to Exit", Toast.LENGTH_SHORT);
    backBeenPressedRunnable = () -> backBeenPressed = false;
    handler.removeCallbacksAndMessages(null);
  }
}

