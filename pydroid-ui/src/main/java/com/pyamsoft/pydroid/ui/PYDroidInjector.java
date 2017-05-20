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
 *
 */

package com.pyamsoft.pydroid.ui;

import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import com.pyamsoft.pydroid.PYDroidModule;
import com.pyamsoft.pydroid.helper.Checker;
import timber.log.Timber;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class PYDroidInjector {

  @Nullable private static volatile PYDroidInjector instance = null;
  @NonNull private final PYDroidComponent component;

  private PYDroidInjector(@NonNull PYDroidModule module) {
    module = Checker.checkNonNull(module);
    this.component = PYDroidComponentImpl.withModule(module);

    UiLicenses.addLicenses();
    if (module.isDebug()) {
      Timber.plant(new Timber.DebugTree());
      setStrictMode();
    }

    Timber.i("Initialize PYDroid Injector singleton");
  }

  @NonNull @CheckResult public static PYDroidComponent with(@NonNull Context context) {
    if (instance == null) {
      synchronized (PYDroidInjector.class) {
        if (instance == null) {
          instance = new PYDroidInjector(new PYDroidModule(context.getApplicationContext()));
        }
      }
    }

    return Checker.checkNonNull(Checker.checkNonNull(instance).component);
  }

  /**
   * Sets strict mode flags when running in debug mode
   */
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
}
