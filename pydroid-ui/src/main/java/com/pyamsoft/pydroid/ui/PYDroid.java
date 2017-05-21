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
import android.support.annotation.VisibleForTesting;
import com.pyamsoft.pydroid.PYDroidModule;
import com.pyamsoft.pydroid.helper.Checker;
import timber.log.Timber;

public final class PYDroid {

  @Nullable private static volatile PYDroid instance = null;
  @NonNull private final PYDroidComponent component;
  private final boolean debug;

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @SuppressWarnings("WeakerAccess") PYDroid(@NonNull PYDroidModule module) {
    module = Checker.checkNonNull(module);
    component = PYDroidComponentImpl.withModule(module);
    debug = module.isDebug();

    UiLicenses.addLicenses();
    if (module.isDebug()) {
      setStrictMode();
      Timber.plant(new Timber.DebugTree());
    }

    Timber.i("Initialize PYDroid Injector singleton");
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @NonNull @CheckResult public static PYDroid getInstance() {
    if (instance == null) {
      synchronized (PYDroid.class) {
        if (instance == null) {
          throw new IllegalStateException(
              "PYDroid instance is NULL, create it first using PYDroid.initialize(Context, boolean)");
        }
      }
    }
    return Checker.checkNonNull(instance);
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @VisibleForTesting
  public static void setInstance(@NonNull PYDroid pyDroid) {
    synchronized (PYDroid.class) {
      Timber.w("Manually settings PYDroid instance");
      instance = pyDroid;
    }
  }

  /**
   * Initialize the library
   */
  public static void initialize(@NonNull Context context, boolean debug) {
    if (instance == null) {
      synchronized (PYDroid.class) {
        if (instance == null) {
          instance = new PYDroid(new PYDroidModule(context.getApplicationContext(), debug));
        }
      }
    }

    if (instance == null) {
      throw new RuntimeException("PYDroid initialization failed!");
    }
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @CheckResult
  public boolean isDebugMode() {
    return debug;
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY)
  @CheckResult @NonNull
  public PYDroidComponent provideComponent() {
    return Checker.checkNonNull(component);
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
