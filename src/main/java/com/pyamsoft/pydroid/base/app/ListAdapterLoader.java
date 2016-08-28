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

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import timber.log.Timber;

public abstract class ListAdapterLoader<T extends FastItemAdapter<?>> extends Loader<T> {

  @Nullable private T adapter;

  protected ListAdapterLoader(@NonNull Context context) {
    super(context);
  }

  @Override protected void onStartLoading() {
    super.onStartLoading();
    if (adapter == null) {
      Timber.d("No cached adapter, force load");
      forceLoad();
    } else {
      Timber.d("Deliver cached adapter");
      deliverResult(adapter);
    }
  }

  @Override protected void onReset() {
    super.onReset();
    if (adapter != null) {
      Timber.d("Destroy resetted adapter");
      adapter.clear();
      adapter = null;
    }
  }

  @Override protected void onForceLoad() {
    super.onForceLoad();
    Timber.d("Force load adapter");
    adapter = loadAdapter();

    Timber.d("Deliver loaded adapter");
    deliverResult(adapter);
  }

  @CheckResult @NonNull protected abstract T loadAdapter();
}
