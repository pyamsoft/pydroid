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

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.about.LicenseProvider;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import timber.log.Timber;

@SuppressLint("Registered") public abstract class PYDroidApplication extends Application
    implements LicenseProvider {

  private RefWatcher refWatcher;
  private PYDroidModule module;

  @NonNull @CheckResult static PYDroidModule get(@NonNull Context context) {
    //noinspection ConstantConditions
    if (context == null) {
      throw new NullPointerException("Context cannot be NULL");
    }

    final Context appContext = context.getApplicationContext();
    if (appContext instanceof PYDroidApplication) {
      return PYDroidApplication.class.cast(appContext).getModule();
    } else {
      throw new ClassCastException("Cannot cast Application Context to IPYDroidApp");
    }
  }

  @CheckResult @NonNull final PYDroidModule getModule() {
    return module;
  }

  @Override public final void onCreate() {
    super.onCreate();
    Timber.i("NEW PYDROID APPLICATION");
    if (BuildConfig.DEBUG) {
      Timber.d("Install live leakcanary");
      refWatcher = LeakCanary.install(this);
    } else {
      refWatcher = RefWatcher.DISABLED;
    }

    module = new PYDroidModule(this);

    createApplicationComponents();
    insertCustomLicensesIntoMap();
  }

  @CallSuper protected void createApplicationComponents() {
  }

  @CheckResult @NonNull public RefWatcher getRefWatcher() {
    if (refWatcher == null) {
      throw new RuntimeException("RefWatcher is NULL");
    }
    return refWatcher;
  }
}
