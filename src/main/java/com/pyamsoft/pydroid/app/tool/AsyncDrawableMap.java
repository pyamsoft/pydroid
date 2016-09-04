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

package com.pyamsoft.pydroid.app.tool;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import rx.Subscription;
import timber.log.Timber;

/**
 * A map that makes it convenient to load AsyncDrawables
 */
public final class AsyncDrawableMap {

  @NonNull private final HashMap<String, Subscription> map;

  public AsyncDrawableMap() {
    this.map = new HashMap<>();
  }

  /**
   * Puts a new element into the map
   *
   * If an old element exists, its task is cancelled first before adding the new one
   */
  public final void put(@NonNull String tag, @NonNull Subscription subscription) {
    if (map.containsKey(tag)) {
      final Subscription old = map.get(tag);
      cancelSubscription(tag, old);
    }

    Timber.d("Insert new subscription for tag: %s", tag);
    map.put(tag, subscription);
  }

  /**
   * Clear all elements in the map
   *
   * If the elements have not been cancelled yet, cancel them before removing them
   */
  public final void clear() {
    for (final Map.Entry<String, Subscription> entry : map.entrySet()) {
      cancelSubscription(entry.getKey(), entry.getValue());
    }

    Timber.d("Clear AsyncDrawableMap");
    map.clear();
  }

  /**
   * Cancels a task
   */
  private void cancelSubscription(@NonNull String tag, @Nullable Subscription subscription) {
    if (subscription != null) {
      if (!subscription.isUnsubscribed()) {
        Timber.d("Unsubscribe for tag: %s", tag);
        subscription.unsubscribe();
      }
    }
  }
}
