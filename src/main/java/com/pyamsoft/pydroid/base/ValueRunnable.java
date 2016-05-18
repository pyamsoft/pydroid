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

public abstract class ValueRunnable<T> implements Runnable {

  @NonNull private WeakReference<T> weakValue;

  public ValueRunnable() {
    this.weakValue = new WeakReference<>(null);
  }

  public final void run(final @NonNull T newValue) {
    setValue(newValue);
    run();
  }

  @Nullable @CheckResult public final T getValue() {
    return weakValue.get();
  }

  public final void setValue(final @NonNull T value) {
    this.weakValue = new WeakReference<>(value);
  }
}
