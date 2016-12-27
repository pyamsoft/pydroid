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
 *
 */

package com.pyamsoft.pydroid;

import android.annotation.SuppressLint;
import android.app.Application;

@SuppressLint("Registered") public abstract class PYDroidApplication extends Application {

  @Override public final void onCreate() {
    super.onCreate();
    if (BuildConfigChecker.getInstance().isDebugMode()) {
      onCreateInDebugMode();
    } else {
      onCreateInReleaseMode();
    }
    onCreateNormalMode();
  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" }) protected void onCreateNormalMode() {

  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" }) protected void onCreateInDebugMode() {

  }

  @SuppressWarnings({ "WeakerAccess", "EmptyMethod" }) protected void onCreateInReleaseMode() {

  }
}
