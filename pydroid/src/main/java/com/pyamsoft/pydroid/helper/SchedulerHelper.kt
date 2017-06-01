/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.helper

import android.support.annotation.CheckResult
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

object SchedulerHelper {

  /**
   * Returns whether the given scheduler is one that runs operations in a background thread
   */
  @JvmStatic @CheckResult private fun isBackgroundScheduler(scheduler: Scheduler): Boolean {
    return scheduler === Schedulers.computation() || scheduler === Schedulers.io() || scheduler === Schedulers.newThread()
  }

  /**
   * Enforce that a given scheduler will run on a background thread
   * OR on Immediate, for Testing
   */
  @JvmStatic fun enforceSubscribeScheduler(scheduler: Scheduler) {
    if (!isBackgroundScheduler(scheduler) && scheduler !== Schedulers.trampoline()) {
      throw RuntimeException("Cannot subscribe on a foreground scheduler")
    }
  }

  /**
   * Enforce that a given scheduler will run on a foreground thread
   * OR on Immediate, for Testing
   */
  @JvmStatic fun enforceObserveScheduler(scheduler: Scheduler) {
    if (isBackgroundScheduler(scheduler)) {
      throw RuntimeException("Cannot observe on a background scheduler")
    }
  }
}
