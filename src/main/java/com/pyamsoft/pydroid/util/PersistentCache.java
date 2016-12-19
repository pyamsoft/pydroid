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
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;
import timber.log.Timber;

public final class PersistentCache {

  @NonNull private static final PersistentCache INSTANCE = new PersistentCache();
  private static final long INVALID_KEY = 0;
  @NonNull private final LongSparseArray<Object> itemCache;
  @NonNull private final Random random;

  @VisibleForTesting PersistentCache() {
    itemCache = new LongSparseArray<>();
    random = new SecureRandom();
  }

  @CheckResult @NonNull public static PersistentCache get() {
    return INSTANCE;
  }

  @CheckResult @VisibleForTesting long generateKey(@NonNull String id,
      @Nullable Bundle savedInstanceState) {
    //noinspection ConstantConditions
    if (id == null || id.isEmpty()) {
      throw new IllegalStateException("Id cannot be NULL or empty");
    }

    final long key;
    if (savedInstanceState == null) {
      // Generate a new key
      key = random.nextLong(;
      Timber.d("Generate new key: %d", key);
    } else {
      // Retrieve the key from the saved instance
      key = savedInstanceState.getLong(id, INVALID_KEY);
      Timber.d("Retrieve stored key from %d", key);
    }

    return key;
  }

  @CheckResult public <T> long load(@NonNull String id, @Nullable Bundle savedInstanceState,
      @NonNull PersistLoader.Callback<T> callback) {
    //noinspection ConstantConditions
    if (savedInstanceState == null) {
      throw new IllegalStateException("Bundle cannot be NULL");
    }

    //noinspection ConstantConditions
    if (id == null || id.isEmpty()) {
      throw new IllegalStateException("Id cannot be NULL or empty");
    }

    //noinspection ConstantConditions
    if (callback == null) {
      throw new IllegalStateException("Callback cannot be NULL");
    }

    // Attempt to fetch the persistent object from the itemCache
    final long key = generateKey(id, savedInstanceState);
    if (key == INVALID_KEY) {
      throw new IllegalStateException("Key is invalid for: " + id);
    }

    final T persist;
    final Object cached = itemCache.get(key);
    // If the persistent object is NULL it did not exist in the itemCache
    if (cached == null) {
      // Load a fresh object
      persist = callback.createLoader().loadPersistent();

      // Save the presenter to the itemCache
      Timber.d("Persist object: %s [%d]", persist, key);
      itemCache.put(key, persist);
    } else {
      try {
        //noinspection unchecked
        persist = (T) cached;
      } catch (ClassCastException e) {
        throw new IllegalStateException("Unable to retrieve cached object", e);
      }
      Timber.d("Loaded cached persistable: %s [%s]", persist, key);
    }

    callback.onPersistentLoaded(persist);

    // Return the key to the caller
    return key;
  }

  /**
   * Saves the generated key into a bundle which will be restored later in the lifecycle
   */
  public <T> void saveKey(@NonNull Bundle outState, @NonNull String id, long key,
      @NonNull Class<T> classType) {
    //noinspection ConstantConditions
    if (outState == null) {
      throw new IllegalStateException("Bundle cannot be NULL");
    }

    //noinspection ConstantConditions
    if (id == null || id.isEmpty()) {
      throw new IllegalStateException("Id cannot be NULL or empty");
    }

    //noinspection ConstantConditions
    if (classType == null) {
      throw new IllegalStateException("ClassType cannot be NULL");
    }

    final long oldKey = outState.getLong(id, INVALID_KEY);
    if (oldKey != INVALID_KEY) {
      final Object oldItem = itemCache.get(oldKey);
      if (!classType.isInstance(oldItem)) {
        throw new IllegalStateException(String.format(Locale.getDefault(),
            "Attempting to save item of type: %s to ID %s [%d] but it already stores an item of type: %s",
            classType, id, key, oldItem.getClass()));
      }
    }

    Timber.d("Save key: %s [%d]", id, key);
    outState.putLong(id, key);
  }

  public void unload(long key) {
    final Object persist = itemCache.get(key);
    if (persist != null) {
      itemCache.remove(key);
      Timber.d("Remove persistable from itemCache: %s [%d]", persist, key);
      if (persist instanceof Destroyable) {
        final Destroyable destroyable = (Destroyable) persist;
        destroyable.destroy();
      }
    } else {
      Timber.e("Persisted object was NULL [%d]", key);
      Timber.e("This is usually indicative of a lifecycle onError. Check your Fragments!");
    }
  }
}
