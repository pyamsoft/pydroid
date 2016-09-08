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

package com.pyamsoft.pydroid.lib;

import android.app.Application;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.pyamsoft.pydroid.BuildConfig;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import timber.log.Timber;

public class PYDroidApplication extends PYDroidApp {

  private PYDroidComponent component;
  private RefWatcher refWatcher;

  @CheckResult @NonNull public static RefWatcher getRefWatcher(@NonNull Fragment fragment) {
    final Application application = fragment.getActivity().getApplication();
    if (application instanceof PYDroidApplication) {
      final PYDroidApplication pyDroidApplication = (PYDroidApplication) application;
      return pyDroidApplication.getRefWatcher();
    } else {
      throw new ClassCastException("Application is not PYDroidApplication");
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

    component = DaggerIPYDroidApp_PYDroidComponent.builder()
        .pYDroidModule(new PYDroidModule(getApplicationContext()))
        .build();
  }

  @CheckResult @NonNull final RefWatcher getRefWatcher() {
    if (refWatcher == null) {
      throw new RuntimeException("RefWatcher is NULL");
    }
    return refWatcher;
  }

  @SuppressWarnings("unchecked") @NonNull @Override public PYDroidComponent provideComponent() {
    return component;
  }
}
