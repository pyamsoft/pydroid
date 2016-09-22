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

package com.pyamsoft.pydroid.util;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.LongSparseArray;
import com.pyamsoft.pydroid.Destroyable;
import com.pyamsoft.pydroid.app.PersistLoader;
import timber.log.Timber;

public final class PersistentCache {

  @NonNull private static final PersistentCache INSTANCE = new PersistentCache();
  @NonNull private final LongSparseArray<Object> cache;

  @VisibleForTesting PersistentCache() {
    cache = new LongSparseArray<>();
  }

  @CheckResult @NonNull public static PersistentCache get() {
    return INSTANCE;
  }

  @CheckResult @VisibleForTesting long generateKey(@NonNull String id,
      @Nullable Bundle savedInstanceState) {
    final long key;
    if (savedInstanceState == null) {
      // Generate a new key
      key = System.nanoTime();
      Timber.d("Generate new key: %d", key);
    } else {
      // Retrieve the key from the saved instance
      key = savedInstanceState.getLong(id, 0);
      Timber.d("Retrieve stored key from %d", key);
    }

    return key;
  }

  @CheckResult public <T> long load(@NonNull String id, @Nullable Bundle savedInstanceState,
      @NonNull PersistLoader.Callback<T> callback) {
    // Attempt to fetch the persistent object from the cache
    final long key = generateKey(id, savedInstanceState);

    @SuppressWarnings("unchecked") T persist = (T) cache.get(key);
    // If the persistent object is NULL it did not exist in the cache
    if (persist == null) {
      // Load a fresh object
      persist = callback.createLoader().loadPersistent();
      Timber.d("Created new persistable: %s [%s]", persist, key);

      // Save the presenter to the cache
      Timber.d("Persist object: %s [%d]", persist, key);
      cache.put(key, persist);
    } else {
      Timber.d("Loaded cached persistable: %s [%s]", persist, key);
    }

    callback.onPersistentLoaded(persist);

    // Return the key to the caller
    return key;
  }

  /**
   * Saves the generated key into a bundle which will be restored later in the lifecycle
   */
  public void saveKey(@NonNull Bundle outState, @NonNull String id, long key) {
    Timber.d("Save key: %s [%d]", id, key);
    outState.putLong(id, key);
  }

  public void unload(long key) {
    final Object persist = cache.get(key);
    if (persist != null) {
      cache.remove(key);
      Timber.d("Remove persistable from cache: %s [%d]", persist, key);
      if (persist instanceof Destroyable) {
        final Destroyable destroyable = (Destroyable) persist;
        destroyable.destroy();
      }
    } else {
      Timber.e("Persisted object was NULL [%d]", key);
      Timber.e("This is usually indicative of a lifecycle error. Check your Fragments!");
    }
  }
}
