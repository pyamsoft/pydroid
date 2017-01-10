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

package com.pyamsoft.pydroid.cache;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pyamsoft.pydroid.Destroyable;
import java.util.HashMap;
import java.util.Map;

public class DefaultPersistentCache extends Cache {

  private Map<String, Object> map;

  @CheckResult @NonNull static DefaultPersistentCache newInstance() {
    final DefaultPersistentCache fragment = new DefaultPersistentCache();
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    map = new HashMap<>();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    //noinspection Convert2streamapi
    for (final String key : map.keySet()) {
      remove(key);
    }
    map = null;
  }

  @Override void put(@NonNull String key, @NonNull Object item) {
    if (map == null) {
      throw new IllegalStateException("Cache must be created first by Activity lifecycle");
    }
    map.put(key, item);
  }

  @Override @CheckResult @Nullable Object get(@NonNull String key) {
    if (map == null) {
      throw new IllegalStateException("Cache must be created first by Activity lifecycle");
    }

    return map.get(key);
  }

  @Override void remove(@NonNull String key) {
    if (map == null) {
      throw new IllegalStateException("Cache must be created first by Activity lifecycle");
    }

    final Object removed = map.remove(key);
    if (removed == null) {
      throw new IllegalStateException("Could not remove, no mapping exists for key: " + key);
    }

    if (removed instanceof Destroyable) {
      ((Destroyable) removed).destroy();
    }
  }
}
