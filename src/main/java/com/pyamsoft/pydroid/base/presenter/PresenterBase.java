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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import java.lang.ref.WeakReference;
import timber.log.Timber;

public abstract class PresenterBase<I> implements Presenter<I> {

  @NonNull private WeakReference<I> weakView = new WeakReference<>(null);

  @NonNull @CheckResult protected final I getView() {
    if (weakView.get() == null) {
      throw new IllegalStateException("No view is bound to this presenter");
    }

    return weakView.get();
  }

  @Override public final void bindView(@NonNull I view) {
    weakView.clear();
    weakView = new WeakReference<>(view);

    Timber.d("Run onBind hook");
    onBind(getView());
  }

  @Override public final void unbindView() {
    Timber.d("Run onUnbind hook");
    onUnbind();
  }

  @Override final public void destroyView() {
    Timber.d("Run onDestroy hook");
    weakView.clear();
    onDestroy();
  }

  protected void onBind(@NonNull I view) {

  }

  protected void onUnbind() {

  }

  protected void onDestroy() {

  }
}
