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

package com.pyamsoft.pydroid.persist;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import timber.log.Timber;

public abstract class PersistLoader<T> {

  /**
   * Even though we do not directly use this context, it is convenient to hold onto for
   * classes which rely on Injection via Dagger (which usually requires a context
   */
  @NonNull private final Context appContext;

  protected PersistLoader(@NonNull Context context) {
    appContext = context.getApplicationContext();
  }

  @NonNull @CheckResult protected final Context getContext() {
    return appContext;
  }

  @CheckResult @NonNull public abstract T loadPersistent();

  public interface Callback<T> {

    @CheckResult @NonNull PersistLoader<T> createLoader();

    void onPersistentLoaded(@NonNull T persist);
  }
}
