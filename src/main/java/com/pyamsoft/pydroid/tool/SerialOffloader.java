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
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.ActionNone;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.FuncNone;

/**
 * An offloader which is executes serially
 */
public class SerialOffloader<T> implements Offloader<T> {

  @Nullable private FuncNone<T> process;
  @Nullable private ActionSingle<Throwable> error;
  @Nullable private ActionSingle<T> result;
  @Nullable private ActionNone finisher;

  @Override @NonNull public Offloader<T> onProcess(@NonNull FuncNone<T> background) {
    if (this.process != null) {
      throw new IllegalStateException("Cannot redefine onProcess action");
    }

    this.process = background;
    return this;
  }

  @Override @NonNull public Offloader<T> onFinish(@NonNull ActionNone finisher) {
    if (this.finisher != null) {
      throw new IllegalStateException("Cannot redefine onFinish action");
    }

    this.finisher = finisher;
    return this;
  }

  @NonNull @Override public Offloader<T> onResult(@NonNull ActionSingle<T> result) {
    if (this.result != null) {
      throw new IllegalStateException("Cannot redefine onResult action");
    }

    this.result = result;
    return this;
  }

  @Override @NonNull public Offloader<T> onError(@NonNull ActionSingle<Throwable> error) {
    if (this.error != null) {
      throw new IllegalStateException("Cannot redefine onError action");
    }

    this.error = error;
    return this;
  }

  @NonNull @Override public ExecutedOffloader execute() {
    if (process == null) {
      throw new NullPointerException("Cannot execute Offloader with NULL onProcess task");
    } else {

      final T o = serialProcess();
      return serialResult(o);
    }
  }

  @CheckResult @Nullable private T serialProcess() {
    if (process == null) {
      throw new NullPointerException("Cannot execute Offloader with NULL onProcess task");
    } else {
      T o;
      try {
        o = process.call();
      } catch (Throwable throwable) {
        if (error == null) {
          throw throwable;
        } else {
          error.call(throwable);
        }
        o = null;
      }

      return o;
    }
  }

  private ExecutedOffloader serialResult(@Nullable T o) {
    try {
      if (result != null && o != null) {
        result.call(o);
      }
    } catch (Throwable throwable) {
      if (error == null) {
        throw throwable;
      } else {
        error.call(throwable);
      }
    }

    if (finisher != null) {
      finisher.call();
    }

    return new Executed();
  }

  @SuppressWarnings("WeakerAccess") static class Executed implements ExecutedOffloader {

    private boolean cancelled = false;

    @Override public boolean isCancelled() {
      return cancelled;
    }

    @Override public void cancel() {
      if (!isCancelled()) {
        cancelled = true;
      }
    }
  }
}
