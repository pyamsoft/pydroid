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

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.pyamsoft.pydroid.FuncNone;

public class PersistentCache {

  @NonNull private static final String CACHE_TAG = "PersistentCacheTag";
  @NonNull private static final PersistentCache INSTANCE = new PersistentCache();

  private PersistentCache() {
  }

  @CheckResult @NonNull
  public static <T> T load(@NonNull FragmentActivity fragmentActivity, @NonNull String key,
      @NonNull FuncNone<T> callback) {
    return INSTANCE.loadItem(fragmentActivity, key, callback);
  }

  public static void unload(@NonNull FragmentActivity fragmentActivity, @NonNull String key) {
    INSTANCE.unloadItem(fragmentActivity, key);
  }

  @CheckResult @NonNull private Cache getImpl(@NonNull Fragment cache) {
    final Cache impl;
    if (cache instanceof Cache) {
      impl = (Cache) cache;
    } else {
      throw new IllegalStateException("PersistentCache is not backed by an object of type Cache");
    }

    return impl;
  }

  @CheckResult @NonNull private <T> T loadPersistedItem(@NonNull Cache impl, @NonNull String key,
      @NonNull FuncNone<T> callback) {
    //noinspection unchecked
    T persistedItem = (T) impl.get(key);
    // Put new item
    if (persistedItem == null) {
      try {
        persistedItem = callback.call();
      } catch (Exception e) {
        throw new RuntimeException("Error while creating PersistentCache item", e);
      }

      // Put into cache
      impl.put(key, persistedItem);
    }

    return persistedItem;
  }

  @CheckResult @NonNull
  private <T> T loadItem(@NonNull FragmentActivity fragmentActivity, @NonNull String key,
      @NonNull FuncNone<T> callback) {
    final FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
    Fragment cache = fragmentManager.findFragmentByTag(CACHE_TAG);
    if (cache == null) {
      cache = DefaultPersistentCache.newInstance();
      fragmentManager.beginTransaction().add(cache, CACHE_TAG).commitNow();
    }

    return loadPersistedItem(getImpl(cache), key, callback);
  }

  private void unloadItem(@NonNull FragmentActivity fragmentActivity, @NonNull String key) {
    final FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
    Fragment cache = fragmentManager.findFragmentByTag(CACHE_TAG);
    if (cache == null) {
      // No cache exists
      return;
    }

    getImpl(cache).remove(key);
  }
}
