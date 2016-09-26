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

package com.pyamsoft.pydroid.presenter;

import android.support.annotation.NonNull;
import java.lang.ref.WeakReference;
import rx.functions.Action1;

public abstract class PresenterBase<I> implements Presenter<I> {

  @NonNull private WeakReference<I> weakView = new WeakReference<>(null);

  /**
   * If the view is non null (bound) then it will be passed into the wrapper function and executed.
   * If the view is null, then this is a no-op
   */
  protected final void getView(@NonNull Action1<I> func) {
    final I view = weakView.get();
    if (view != null) {
      func.call(view);
    }
  }

  @Override public final boolean isBound() {
    return weakView.get() != null;
  }

  @Override public final void bindView(@NonNull I view) {
    weakView.clear();
    weakView = new WeakReference<>(view);
    onBind();
  }

  @Override public final void unbindView() {
    weakView.clear();
    onUnbind();
  }

  @Override final public void destroy() {
    onDestroy();
  }

  protected void onBind() {

  }

  protected void onUnbind() {

  }

  protected void onDestroy() {

  }
}
