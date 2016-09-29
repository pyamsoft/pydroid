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

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.ActionSingle;
import com.pyamsoft.pydroid.FuncNone;

/**
 * An offloader which is backed by an AsyncTask
 */
public class OffloaderAsyncTask<T> implements Offloader<T> {

  @SuppressWarnings("WeakerAccess") @Nullable FuncNone<T> background;
  @SuppressWarnings("WeakerAccess") @Nullable ActionSingle<T> result;
  @SuppressWarnings("WeakerAccess") @Nullable ActionSingle<Throwable> error;
  @Nullable private AsyncTask asnycTask;

  @Override @NonNull public Offloader<T> background(@NonNull FuncNone<T> background) {
    if (this.background != null) {
      throw new IllegalStateException("Cannot redefine background action");
    }

    this.background = background;
    return this;
  }

  @Override @NonNull public Offloader<T> result(@NonNull ActionSingle<T> result) {
    if (this.result != null) {
      throw new IllegalStateException("Cannot redefine result action");
    }

    this.result = result;
    return this;
  }

  @Override @NonNull public Offloader<T> error(@NonNull ActionSingle<Throwable> error) {
    if (this.error != null) {
      throw new IllegalStateException("Cannot redefine error action");
    }

    this.error = error;
    return this;
  }

  @Override public boolean isCancelled() {
    return asnycTask == null || asnycTask.isCancelled();
  }

  @Override public void cancel() {
    if (asnycTask != null) {
      if (!asnycTask.isCancelled()) {
        asnycTask.cancel(true);
      }
    }
  }

  @NonNull @Override public Offloader<T> execute() {
    if (background == null) {
      throw new NullPointerException("Cannot execute Offloader with NULL background task");
    } else {
      cancel();
      asnycTask = new AsyncTask<Void, Void, T>() {
        @Override protected T doInBackground(Void... params) {
          try {
            return background.call();
          } catch (Throwable throwable) {
            if (error == null) {
              throw throwable;
            } else {
              error.call(throwable);
            }
            return null;
          }
        }

        @Override protected void onPostExecute(T o) {
          super.onPostExecute(o);
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
        }
      };

      return this;
    }
  }
}
