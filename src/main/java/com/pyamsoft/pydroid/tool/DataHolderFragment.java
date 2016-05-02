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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/**
 * A fragment that retains data,
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) public final class DataHolderFragment<T>
    extends Fragment {

  @NonNull private static final String TAG = DataHolderFragment.class.getSimpleName();
  @NonNull private final SparseArray<T> sparseArray = new SparseArray<>();

  /**
   * Remove all DataHolderFragments from the Fragment Manager
   */
  @SuppressWarnings("Convert2streamapi") public static void removeAll(final @NonNull FragmentActivity fragmentActivity) {
    final FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
    final List<Fragment> fragmentList = fragmentManager.getFragments();
    for (final Fragment fragment : fragmentList) {
      if (fragment instanceof DataHolderFragment) {
        final DataHolderFragment dataHolderFragment = (DataHolderFragment) fragment;
        dataHolderFragment.remove();
      }
    }
  }

  /**
   * Get an Instance of DataHolderFragment
   */
  @SuppressWarnings("unchecked") public static <I> DataHolderFragment<I> getInstance(
      final @NonNull FragmentActivity fragmentActivity, final @NonNull Class<I> clazz) {
    return getInstance(fragmentActivity.getSupportFragmentManager(), clazz);
  }

  /**
   * Get an Instance of DataHolderFragment
   */
  @SuppressWarnings("unchecked") public static <I> DataHolderFragment<I> getInstance(
      final @NonNull FragmentManager fragmentManager, final @NonNull Class<I> clazz) {
    final String tag = clazz.getName();
    DataHolderFragment<I> dataHolderFragment =
        (DataHolderFragment<I>) fragmentManager.findFragmentByTag(tag);
    if (dataHolderFragment == null) {
      Timber.d("Create a new DataHolderFragment with TAG: %s", tag);
      dataHolderFragment = new DataHolderFragment<>();
      fragmentManager.beginTransaction().add(dataHolderFragment, tag).commit();
    }
    return dataHolderFragment;
  }

  /**
   * Get an Instance of DataHolderFragment
   */
  @SuppressWarnings("unchecked")
  public static <I> DataHolderFragment<Collection<I>> getCollectionInstance(
      final @NonNull FragmentActivity fragmentActivity, final @NonNull Class<I> clazz) {
    return getCollectionInstance(fragmentActivity.getSupportFragmentManager(), clazz);
  }

  /**
   * Get an Instance of DataHolderFragment
   */
  @SuppressWarnings("unchecked")
  public static <I> DataHolderFragment<Collection<I>> getCollectionInstance(
      final @NonNull FragmentManager fragmentManager, final @NonNull Class<I> clazz) {
    final String tag = clazz.getName();
    DataHolderFragment<Collection<I>> dataHolderFragment =
        (DataHolderFragment<Collection<I>>) fragmentManager.findFragmentByTag(tag);
    if (dataHolderFragment == null) {
      Timber.d("Create a new DataHolderFragment<Collection> with TAG: %s", tag);
      dataHolderFragment = new DataHolderFragment<>();
      fragmentManager.beginTransaction().add(dataHolderFragment, tag).commit();
    }
    return dataHolderFragment;
  }

  /**
   * Get an Instance of DataHolderFragment
   */
  @SuppressWarnings("unchecked") public static <K, V> DataHolderFragment<Map<K, V>> getMapInstance(
      final @NonNull FragmentActivity fragmentActivity, final @NonNull Class<K> keyClass,
      final @NonNull Class<V> valueClass) {
    return getMapInstance(fragmentActivity.getSupportFragmentManager(), keyClass, valueClass);
  }

  /**
   * Get an Instance of DataHolderFragment
   */
  @SuppressWarnings("unchecked") public static <K, V> DataHolderFragment<Map<K, V>> getMapInstance(
      final @NonNull FragmentManager fragmentManager, final @NonNull Class<K> keyClass,
      final @NonNull Class<V> valueClass) {
    final String tag = keyClass.getName() + "|" + valueClass.getName();
    DataHolderFragment<Map<K, V>> dataHolderFragment =
        (DataHolderFragment<Map<K, V>>) fragmentManager.findFragmentByTag(tag);
    if (dataHolderFragment == null) {
      Timber.d("Create a new DataHolderFragment<Map> with TAG: %s", tag);
      dataHolderFragment = new DataHolderFragment<>();
      fragmentManager.beginTransaction().add(dataHolderFragment, tag).commit();
    }
    return dataHolderFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Timber.d("Create DataHolderFragment");
    setRetainInstance(true);
  }

  @Override public void onDestroy() {
    super.onDestroy();

    Timber.d("Destroy DataHolderFragment");
    clear();
  }

  /**
   * Clear the sparse array
   */
  public final void clear() {
    Timber.d("Clear data sparseArray");
    sparseArray.clear();
  }

  /**
   * Puts a new Value into the table at a given key
   *
   * It is up to the caller to remember the key
   */
  public final void put(final int key, final T value) {
    Timber.d("Put value: %s into key: %d", value, key);
    sparseArray.put(key, value);
  }

  /**
   * Retrieves the value from the table and then removes the object
   */
  @Nullable public final T get(final int key) {
    Timber.d("Pop value at key: %d", key);
    final T result = sparseArray.get(key);
    sparseArray.remove(key);
    return result;
  }

  /**
   * Remove the data holder fragment from the Activity
   *
   * Usually called in onDestroy
   */
  public final void remove() {
    clear();
    getFragmentManager().beginTransaction().remove(this).commit();
  }
}
