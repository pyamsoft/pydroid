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
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

@SuppressLint("Registered") public class TestPYDroidApplication
    extends IPYDroidApp<PYDroidComponent> {

  private PYDroidComponent component;

  @NonNull @CheckResult static IPYDroidApp<PYDroidComponent> get(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    if (appContext instanceof IPYDroidApp) {
      return TestPYDroidApplication.class.cast(appContext);
    } else {
      throw new ClassCastException("Cannot cast Application Context to IPYDroidApp");
    }
  }

  @Override public void onCreate() {
    super.onCreate();

    component = DaggerPYDroidComponent.builder()
        .pYDroidModule(new PYDroidModule(getApplicationContext()))
        .build();
  }

  @NonNull @Override PYDroidComponent provideComponent() {
    if (component == null) {
      throw new NullPointerException("TestPYDroidComponent is NULL");
    }
    return component;
  }
}
