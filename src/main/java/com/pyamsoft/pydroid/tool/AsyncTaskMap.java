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
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

/**
 * A map that holds Async Task objects and has helper methods to add new ones as well as
 * clear the stored map
 */
public final class AsyncTaskMap {

  @NonNull private final HashMap<String, AsyncTask> taskMap;

  public AsyncTaskMap() {
    this.taskMap = new HashMap<>();
  }

  /**
   * Puts a new element into the map
   *
   * If an old element exists, its task is cancelled first before adding the new one
   */
  public final void put(@NonNull String tag, @NonNull AsyncTask task) {
    if (taskMap.containsKey(tag)) {
      final AsyncTask oldTask = taskMap.get(tag);
      Timber.d("Cancel old task for tag: %s", tag);
      cancelTask(tag, oldTask);
    }

    Timber.d("Insert new task for tag: %s", tag);
    taskMap.put(tag, task);
  }

  /**
   * Clear all elements in the map
   *
   * If the elements have not been cancelled yet, cancel them before removing them
   */
  public final void clear() {
    Timber.d("Cancel task map tasks");
    for (final Map.Entry<String, AsyncTask> entry : taskMap.entrySet()) {
      cancelTask(entry.getKey(), entry.getValue());
    }

    Timber.d("Clear task map");
    taskMap.clear();
  }

  /**
   * Cancels an Async task
   */
  private void cancelTask(@NonNull String tag, @Nullable AsyncTask task) {
    if (task != null) {
      if (!task.isCancelled()) {
        Timber.d("Cancel AsyncTask with tag: %s", tag);
        task.cancel(true);
      }
    }
  }
}
