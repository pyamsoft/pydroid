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

package com.pyamsoft.pydroid.ui.loader;

import android.support.annotation.NonNull;
import com.pyamsoft.pydroid.helper.Checker;
import com.pyamsoft.pydroid.ui.loader.loaded.Loaded;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class LoaderMap {

  @NonNull private final HashMap<String, Loaded> map;

  public LoaderMap() {
    this.map = new HashMap<>();
  }

  /**
   * Puts a new element into the map
   *
   * If an old element exists, its task is cancelled first before adding the new one
   */
  public final void put(@NonNull String tag, @NonNull Loaded subscription) {
    tag = Checker.checkNonNull(tag);
    subscription = Checker.checkNonNull(subscription);

    if (map.containsKey(tag)) {
      map.put(tag, LoaderHelper.unload(map.get(tag)));
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
    for (final Map.Entry<String, Loaded> entry : map.entrySet()) {
      Loaded value = Checker.checkNonNull(entry.getValue());
      entry.setValue(LoaderHelper.unload(value));
    }

    Timber.d("Clear AsyncDrawableMap");
    map.clear();
  }
}
