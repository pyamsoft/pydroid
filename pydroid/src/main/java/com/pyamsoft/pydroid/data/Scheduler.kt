/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.data

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun Scheduler.enforceComputation() {
  if (this != Schedulers.computation()) {
    throw RuntimeException("Scheduler is not computation scheduler: $this")
  }
}

fun Scheduler.enforceIo() {
  if (this != Schedulers.io()) {
    throw RuntimeException("Scheduler is not io scheduler: $this")
  }
}

fun Scheduler.enforceMainThread() {
  if (this != AndroidSchedulers.mainThread()) {
    throw RuntimeException("Scheduler is not Android mainThread scheduler: $this")
  }
}
