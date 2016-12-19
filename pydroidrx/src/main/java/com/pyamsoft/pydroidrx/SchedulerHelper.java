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

package com.pyamsoft.pydroidrx;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public final class SchedulerHelper {

  private SchedulerHelper() {
    throw new RuntimeException("No instances");
  }

  /**
   * Returns whether the given scheduler is one that runs operations in a background thread
   */
  @CheckResult private static boolean isBackgroundScheduler(@NonNull Scheduler scheduler) {
    //noinspection ConstantConditions
    if (scheduler == null) {
      throw new NullPointerException("Scheduler cannot be NULL");
    }

    return scheduler == Schedulers.computation()
        || scheduler == Schedulers.io()
        || scheduler == Schedulers.newThread();
  }

  /**
   * Enforce that a given scheduler will run on a background thread
   *
   * OR on Immediate, for Testing
   */
  public static void enforceSubscribeScheduler(@NonNull Scheduler scheduler) {
    if (!isBackgroundScheduler(scheduler) && scheduler != Schedulers.immediate()) {
      throw new RuntimeException("Cannot subscribe on a foreground scheduler");
    }
  }

  /**
   * Enforce that a given scheduler will run on a foreground thread
   *
   * OR on Immediate, for Testing
   */
  public static void enforceObserveScheduler(@NonNull Scheduler scheduler) {
    if (isBackgroundScheduler(scheduler)) {
      throw new RuntimeException("Cannot observe on a background scheduler");
    }
  }
}
