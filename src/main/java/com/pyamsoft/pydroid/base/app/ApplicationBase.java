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

package com.pyamsoft.pydroid.base.app;

import android.app.Application;
import android.support.annotation.CheckResult;
import com.pyamsoft.pydroid.crash.CrashHandler;
import timber.log.Timber;

public abstract class ApplicationBase extends Application implements CrashHandler.Provider {

  @Override public void onCreate() {
    super.onCreate();

    // Fix for Bug 81083 GPS Bug
    try {
      Class.forName("android.os.AsyncTask");
    } catch (final Throwable ignore) {
      // ignored
    }

    if (buildConfigDebug()) {
      Timber.plant(new Timber.DebugTree());
    }
  }

  @CheckResult protected abstract boolean buildConfigDebug();

  /**
   * Override for custom crash log text
   */
  @Override public String crashLogText() {
    return null;
  }

  /**
   * Override for custom crash log subject
   */
  @Override public String crashLogSubject() {
    return null;
  }
}
