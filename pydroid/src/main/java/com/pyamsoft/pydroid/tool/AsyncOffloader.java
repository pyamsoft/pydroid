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

package com.pyamsoft.pydroid.tool;

import android.os.AsyncTask;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import com.google.common.util.concurrent.ExecutionError;
import com.pyamsoft.pydroid.ActionNone;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.FuncNone;

/**
 * An offloader which is backed by an AsyncTask
 */
public class AsyncOffloader<T> implements Offloader<T> {

  @SuppressWarnings("WeakerAccess") @Nullable FuncNone<T> process;
  @SuppressWarnings("WeakerAccess") @Nullable ActionSingle<Throwable> error;
  @SuppressWarnings("WeakerAccess") @Nullable ActionSingle<T> result;
  @SuppressWarnings("WeakerAccess") @Nullable ActionNone finisher;
  @Nullable private AsyncTask asnycTask;

  private AsyncOffloader() {

  }

  @CheckResult @NonNull public static <T> Offloader<T> newInstance(@NonNull FuncNone<T> process) {
    return new AsyncOffloader<T>().onProcess(process);
  }

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

  @Override @NonNull public Offloader<T> onError(@NonNull ActionSingle<Throwable> error) {
    if (this.error != null) {
      throw new IllegalStateException("Cannot redefine onError action");
    }

    this.error = error;
    return this;
  }

  @NonNull @Override public Offloader<T> onResult(@NonNull ActionSingle<T> result) {
    if (this.result != null) {
      throw new IllegalStateException("Cannot redefine onFinish action");
    }

    this.result = result;
    return this;
  }

  @NonNull @Override public ExecutedOffloader execute() {
    if (process == null) {
      throw new NullPointerException("Cannot execute Offloader with NULL onProcess task");
    } else {
      asnycTask = AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, T>() {
        @Override protected T doInBackground(Void... params) {
          try {
            return process.call();
          } catch (Exception e) {
            if (error == null) {
              throw new RuntimeException("Captured exception in Offloader", e);
            } else {
              error.call(e);
            }
            return null;
          }
        }

        @Override protected void onPostExecute(T o) {
          super.onPostExecute(o);
          if (result != null && o != null) {
            result.call(o);
          }

          if (finisher != null) {
            finisher.call();
          }
        }
      });

      return new Executed(asnycTask);
    }
  }

  @SuppressWarnings("WeakerAccess") static class Executed implements ExecutedOffloader {

    @NonNull private final AsyncTask task;

    Executed(@NonNull AsyncTask asyncTask) {
      this.task = asyncTask;
    }

    @Override public boolean isCancelled() {
      return task.isCancelled();
    }

    @Override public void cancel() {
      if (!task.isCancelled()) {
        task.cancel(true);
      }
    }
  }
}
