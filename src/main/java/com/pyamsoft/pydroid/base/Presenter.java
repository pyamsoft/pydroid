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

package com.pyamsoft.pydroid.base;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import timber.log.Timber;

public abstract class Presenter<I> {

  @Nullable private I view;

  protected Presenter() {
  }

  @CheckResult @NonNull protected final I getView() {
    if (view == null) {
      throw new IllegalStateException("Cannot call getView() on a null View");
    }
    return view;
  }

  public final void bindView(@NonNull I view) {
    bindView(view, true);
  }

  public final void bindView(@NonNull I view, boolean runHook) {
    if (this.view != null) {
      throw new IllegalStateException("Must call unbindView before calling bindView again");
    }
    this.view = view;

    if (runHook) {
      Timber.d("Run onBind hook");
      onBind();
    }
  }

  public final void unbindView() {
    unbindView(true);
  }

  public final void unbindView(boolean runHook) {
    if (runHook) {
      Timber.d("Run onUnbind hook");
      onUnbind();
    }

    if (this.view == null) {
      throw new IllegalStateException("Must call bindView before calling unbindView again.");
    }
    this.view = null;
  }

  public void onResume() {

  }

  public void onPause() {

  }

  protected void onBind() {

  }

  protected void onUnbind() {

  }
}
