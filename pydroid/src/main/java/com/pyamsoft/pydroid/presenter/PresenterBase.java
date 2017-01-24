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

package com.pyamsoft.pydroid.presenter;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.ActionSingle;
import java.lang.ref.WeakReference;

public abstract class PresenterBase<I> implements Presenter<I> {

  @NonNull private WeakReference<I> weakView = new WeakReference<>(null);

  /**
   * If the view is non null (bound) then it will be passed into the wrapper function and executed.
   * If the view is null, then this is a no-op
   */
  protected final void getView(@NonNull ActionSingle<I> func) {
    final I view = weakView.get();
    if (view != null) {
      func.call(view);
    }
  }

  /**
   * Called when the presenter attaches to the view
   */
  @Override public final void bindView(@NonNull I view) {
    weakView.clear();
    weakView = new WeakReference<>(view);
    onBind();
  }

  /**
   * Called when the presenter detaches from the view
   */
  @Override public final void unbindView() {
    onUnbind();
    weakView.clear();
  }

  /**
   * Called when the presenter is destroyed and all memory released
   */
  @Override public final void destroyView() {
    onDestroy();
  }

  /**
   * Called once the view has been bound
   *
   * Calls to the view can by asynchronous, but make no guarantees about a view being available.
   */
  protected void onBind() {

  }

  /**
   * Called once the view will be unbound
   *
   * Calls to the view must be synchronous, but make no guarantees about a view being available
   */
  protected void onUnbind() {

  }

  /**
   * Called when the presenter is to be destroyed
   *
   * View is not accessible.
   */
  protected void onDestroy() {

  }
}
