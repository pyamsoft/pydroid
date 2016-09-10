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
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.google.firebase.FirebaseApp;
import com.pyamsoft.pydroid.BuildConfig;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import timber.log.Timber;

public class PYDroidApplication extends IPYDroidApp {

  private PYDroidComponent component;
  private RefWatcher refWatcher;

  @NonNull @CheckResult static IPYDroidApp get(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    if (appContext instanceof IPYDroidApp) {
      return (IPYDroidApp) appContext;
    } else {
      throw new ClassCastException("Cannot cast Application Context to IPYDroidApp");
    }
  }

  @CheckResult @NonNull public static RefWatcher getRefWatcher(@NonNull Fragment fragment) {
    final Application application = fragment.getActivity().getApplication();
    if (application instanceof PYDroidApplication) {
      final PYDroidApplication pyDroidApplication = (PYDroidApplication) application;
      return pyDroidApplication.getRefWatcher();
    } else {
      throw new ClassCastException("Application is not PYDroidApplication");
    }
  }

  @Override public final void onCreate() {
    super.onCreate();
    Timber.w("NEW PYDROID APPLICATION");
    if (!FirebaseApp.getApps(getApplicationContext()).isEmpty()) {
      Timber.i("INIT NEW FIREBASE INSTANCE");
      onFirstCreate();
    }
  }

  @CheckResult @NonNull final RefWatcher getRefWatcher() {
    if (refWatcher == null) {
      throw new RuntimeException("RefWatcher is NULL");
    }
    return refWatcher;
  }

  @NonNull @Override PYDroidComponent provideComponent() {
    if (component == null) {
      throw new NullPointerException("PYDroidComponent is NULL");
    }
    return component;
  }

  @CheckResult @NonNull PYDroidComponent provide(Class<PYDroidComponent> clazz) {
    if (component == null) {
      throw new NullPointerException("PYDroidComponent is NULL");
    }
    return clazz.cast(component);
  }

  /**
   * In a Firebase multi process app, this block of code will be guaranteed to only run on the
   * first
   * application creation instance
   */
  @CallSuper protected void onFirstCreate() {
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
}
