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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class Presenter {

  @NonNull private final CompositeDisposable stopDisposables;
  @NonNull private final CompositeDisposable destroyDisposables;

  protected Presenter() {
    stopDisposables = new CompositeDisposable();
    destroyDisposables = new CompositeDisposable();
  }

  public final void stop() {
    onStop();
    stopDisposables.clear();
  }

  public final void destroy() {
    onDestroy();
    destroyDisposables.clear();
  }

  /**
   * Override per implementation
   */
  protected void onStop() {

  }

  /**
   * Override per implementation
   */
  protected void onDestroy() {

  }

  /**
   * Add a disposable to the internal list, dispose it onStop
   */
  protected final void disposeOnStop(Disposable disposable) {
    disposeOnLifecycleEvent(stopDisposables, disposable);
  }

  /**
   * Add a disposable to the internal list, dispose it onDestroy
   */
  protected final void disposeOnDestroy(Disposable disposable) {
    disposeOnLifecycleEvent(destroyDisposables, disposable);
  }

  /**
   * Adds a disposable to a composite if the disposable is not NULL
   */
  private void disposeOnLifecycleEvent(@NonNull CompositeDisposable disposables,
      Disposable disposable) {
    if (disposable != null) {
      disposables.add(disposable);
    }
  }
}
