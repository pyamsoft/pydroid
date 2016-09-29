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

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;
import com.pyamsoft.pydroid.ActionSingle;
import java.util.HashSet;
import java.util.Set;
import timber.log.Timber;

public class BusImpl<T> implements Bus<T> {

  @NonNull private final Set<Pair<Event<T>, Error>> observers;
  @NonNull private final Handler handler;

  @VisibleForTesting public BusImpl() {
    observers = new HashSet<>();
    handler = new Handler(Looper.getMainLooper());
  }

  @Override public void post(@NonNull T event) {
    if (!observers.isEmpty()) {
      for (final Pair<Event<T>, Error> pair : observers) {
        if (pair != null) {
          final Event<T> first = pair.first;
          final Error second = pair.second;
          handler.post(() -> {
            if (first != null) {
              try {
                Timber.d("Post event to observer");
                first.call(event);
              } catch (Throwable t) {
                Timber.w("Unregister onError");
                unregister(first);

                if (second == null) {
                  throw t;
                } else {
                  second.call(t);
                }
              }
            }
          });
        }
      }
    }
  }

  private void remove(@NonNull ActionSingle<T> onCall) {
    for (final Pair<Event<T>, Error> pair : observers) {
      if (onCall.equals(pair.first)) {
        observers.remove(pair);
        break;
      }
    }
  }

  @Override @CheckResult @NonNull public Event<T> register(@NonNull Event<T> onCall) {
    return register(onCall, null);
  }

  @Override @CheckResult @NonNull
  public Event<T> register(@NonNull Event<T> onCall, @Nullable Error onError) {
    remove(onCall);
    observers.add(new Pair<>(onCall, onError));
    return onCall;
  }

  @Override public void unregister(@Nullable Event<T> onCall) {
    if (onCall != null) {
      remove(onCall);
    }
  }
}
