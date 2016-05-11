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
import java.lang.ref.WeakReference;

public abstract class PresenterImpl<I> implements Presenter<I> {

  @NonNull private WeakReference<I> weakView;

  protected PresenterImpl() {
    this.weakView = new WeakReference<>(null);
  }

  @CheckResult @Nullable protected final I getView() {
    return weakView.get();
  }

  @Override public void onCreateView(@NonNull I view) {
    this.weakView = new WeakReference<>(view);
  }

  @Override public void onDestroyView() {
    weakView.clear();
  }

  @Override public void onResume() {

  }

  @Override public void onPause() {

  }
}
