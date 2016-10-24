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

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.about.AboutLibrariesPresenter;
import com.pyamsoft.pydroid.app.PersistLoader;

public abstract class AboutLibrariesLoaderCallback
    implements PersistLoader.Callback<AboutLibrariesPresenter> {

  @NonNull private final Context context;

  protected AboutLibrariesLoaderCallback(@NonNull Context context) {
    //noinspection ConstantConditions
    if (context == null) {
      throw new NullPointerException("Context cannot be NULL");
    }
    this.context = context.getApplicationContext();
  }

  @NonNull @Override public PersistLoader<AboutLibrariesPresenter> createLoader() {
    return PYDroidApplication.get(context).provideAboutLibrariesModule().getLoader();
  }
}
