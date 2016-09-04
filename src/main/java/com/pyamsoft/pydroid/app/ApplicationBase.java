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

package com.pyamsoft.pydroid.app;

import android.app.Application;
import android.os.StrictMode;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import timber.log.Timber;

public abstract class ApplicationBase extends Application {

  @CheckResult protected boolean isTest() {
    return false;
  }

  @CallSuper @Override public void onCreate() {
    super.onCreate();

    if (!isTest()) {
      if (buildConfigDebug()) {
        installInDebugMode();
      }
    }
  }

  /**
   * A hook that one can use to setup any special application handling in debug mode
   */
  @CallSuper protected void installInDebugMode() {
    Timber.plant(new Timber.DebugTree());
    setStrictMode();
  }

  private void setStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
        .penaltyLog()
        .penaltyDeath()
        .permitDiskReads()
        .permitDiskWrites()
        .penaltyFlashScreen()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
  }

  @CheckResult protected abstract boolean buildConfigDebug();
}
