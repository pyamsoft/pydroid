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

package com.pyamsoft.pydroid.tool;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.FuncNone;

/**
 * A class which can help with offloading work to the background
 *
 * Cancel does not guarantee immediate cancelling, do not rely too heavily on this class, think of
 * it more as a way to abstract hard dependency on AsyncTask out of code base
 *
 * Requires a background function, but result and error handling are optional
 */
public interface Offloader<T> {

  @CheckResult boolean isCancelled();

  void cancel();

  @CheckResult @NonNull Offloader<T> background(@NonNull FuncNone<T> background);

  @CheckResult @NonNull Offloader<T> result(@NonNull ActionSingle<T> result);

  @CheckResult @NonNull Offloader<T> error(@NonNull ActionSingle<Throwable> error);

  @CheckResult @NonNull Offloader<T> execute();

  class Empty<T> implements Offloader<T> {

    @Override public boolean isCancelled() {
      return false;
    }

    @Override public void cancel() {

    }

    @NonNull @Override public Offloader<T> background(@NonNull FuncNone<T> background) {
      return this;
    }

    @NonNull @Override public Offloader<T> result(@NonNull ActionSingle<T> result) {
      return this;
    }

    @NonNull @Override public Offloader<T> error(@NonNull ActionSingle<Throwable> error) {
      return this;
    }

    @NonNull @Override public Offloader<T> execute() {
      return this;
    }
  }
}
