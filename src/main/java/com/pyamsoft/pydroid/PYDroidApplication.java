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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import timber.log.Timber;

@SuppressLint("Registered") public abstract class PYDroidApplication extends Application {

  private RefWatcher refWatcher;

  @Override public final void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.d("Install live leakcanary");
      refWatcher = LeakCanary.install(this);
      onCreateInDebugMode();
    } else {
      refWatcher = RefWatcher.DISABLED;
    }

    onCreateInNormalMode();
  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" }) protected void onCreateInDebugMode() {

  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" }) protected void onCreateInNormalMode() {

  }

  @CheckResult @NonNull public RefWatcher getRefWatcher() {
    if (refWatcher == null) {
      throw new RuntimeException("RefWatcher is NULL");
    }
    return refWatcher;
  }
}
