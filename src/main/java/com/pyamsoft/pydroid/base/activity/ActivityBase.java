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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.pyamsoft.pydroid.util.IMMLeakUtil;

public abstract class ActivityBase extends AppCompatActivity {

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

  void setupFakeFullscreenWindow() {
    getWindow().getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
  }

  @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    // These must go before the call to onCreate
    if (shouldHandleIMMLeaks()) {
      IMMLeakUtil.fixFocusedViewLeak(getApplication());
    }
    if (isFakeFullscreen()) {
      setupFakeFullscreenWindow();
    }

    super.onCreate(savedInstanceState);
  }
}

