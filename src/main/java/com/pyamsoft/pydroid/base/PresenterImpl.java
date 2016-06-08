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

public abstract class PresenterImpl<I> implements Presenter<I> {

  @Nullable private I view;

  protected PresenterImpl() {
  }

  @CheckResult @NonNull protected final I getView() {
    if (view == null) {
      throw new IllegalStateException("Cannot call getView() on a null View");
    }
    return view;
  }

  @Override public void onCreateView(@NonNull I view) {
    if (this.view != null) {
      throw new IllegalStateException("Must call onDestroyView before calling onCreateView again");
    }
    this.view = view;
  }

  @Override public void onDestroyView() {
    if (this.view == null) {
      throw new IllegalStateException("Must call onCreateView before calling onDestroyView again.");
    }
    this.view = null;
  }

  @Override public void onResume() {

  }

  @Override public void onPause() {

  }
}
