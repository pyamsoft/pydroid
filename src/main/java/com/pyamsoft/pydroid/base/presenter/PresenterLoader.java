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

package com.pyamsoft.pydroid.base.presenter;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import timber.log.Timber;

public abstract class PresenterLoader<T extends Presenter<?>> extends Loader<T> {

  @Nullable private T presenter;

  public PresenterLoader(@NonNull Context context) {
    super(context);
  }

  @Override protected void onStartLoading() {
    super.onStartLoading();
    if (presenter == null) {
      Timber.d("No cached presenter, force load");
      forceLoad();
    } else {
      Timber.d("Deliver cached presenter");
      deliverResult(presenter);
    }
  }

  @Override protected void onReset() {
    super.onReset();
    if (presenter != null) {
      Timber.d("Destroy resetted presenter");
      presenter.destroyView();
      presenter = null;
    }
  }

  @Override protected void onForceLoad() {
    super.onForceLoad();
    Timber.d("Force load presenter");
    presenter = loadPresenter();

    Timber.d("Deliver loaded presenter");
    deliverResult(presenter);
  }

  @CheckResult @NonNull protected abstract T loadPresenter();
}
