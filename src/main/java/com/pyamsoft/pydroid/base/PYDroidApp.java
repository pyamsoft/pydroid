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

package com.pyamsoft.pydroid.base;

import android.app.Application;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.pyamsoft.pydroid.BuildConfig;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import timber.log.Timber;

abstract class PYDroidApp extends Application {

  private RefWatcher refWatcher;

  @CheckResult @NonNull static RefWatcher getRefWatcher(@NonNull Fragment fragment) {
    final PYDroidApp application = (PYDroidApp) fragment.getActivity().getApplication();
    return application.getRefWatcher();
  }

  @NonNull @CheckResult static PYDroidApp get(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    if (appContext instanceof PYDroidApp) {
      return (PYDroidApp) appContext;
    } else {
      throw new ClassCastException("Cannot cast Application Context to PadLockBase");
    }
  }

  @Override public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.d("Install live leakcanary");
      refWatcher = LeakCanary.install(this);
    } else {
      refWatcher = RefWatcher.DISABLED;
    }
  }

  @CheckResult @NonNull final RefWatcher getRefWatcher() {
    if (refWatcher == null) {
      throw new RuntimeException("RefWatcher is NULL");
    }
    return refWatcher;
  }

  @CheckResult @NonNull abstract <T extends PYDroidComponent> T provideComponent();
}
