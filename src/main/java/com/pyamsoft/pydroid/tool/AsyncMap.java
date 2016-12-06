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
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class AsyncMap<T extends AsyncMap.Entry> {

  @NonNull private final HashMap<String, T> map;

  public AsyncMap() {
    this.map = new HashMap<>();
  }

  /**
   * Puts a new element into the map
   *
   * If an old element exists, its task is cancelled first before adding the new one
   */
  public final void put(@NonNull String tag, @NonNull T subscription) {
    if (map.containsKey(tag)) {
      AsyncMapHelper.unsubscribe(map.get(tag));
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
    for (final Map.Entry<String, T> entry : map.entrySet()) {
      AsyncMapHelper.unsubscribe(entry.getValue());
    }

    Timber.d("Clear AsyncDrawableMap");
    map.clear();
  }

  public interface Entry {
    void unload();

    @CheckResult boolean isUnloaded();
  }
}
