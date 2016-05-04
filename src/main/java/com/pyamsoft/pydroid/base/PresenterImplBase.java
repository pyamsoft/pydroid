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

import android.support.annotation.NonNull;
import java.lang.ref.WeakReference;

public abstract class PresenterImplBase<I> implements PresenterBase<I> {

  private WeakReference<I> weakView;

  @NonNull protected final I get() throws NullPointerException {
    final I view = weakView.get();
    if (view == null) {
      throw new NullPointerException("Presenter VIEW is NULL");
    }

    return view;
  }

  @Override public void create() {

  }

  @Override public void destroy() {

  }

  @Override public void bind(@NonNull I view) {
    this.weakView = new WeakReference<>(view);
  }

  @Override public void unbind() {
    weakView.clear();
  }
}
