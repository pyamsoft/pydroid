/*
 * Copyright 2019 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bootstrap

import androidx.annotation.CheckResult
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface SchedulerProvider {

  @get:CheckResult
  val backgroundScheduler: Scheduler

  @get:CheckResult
  val foregroundScheduler: Scheduler

  object DEFAULT : SchedulerProvider {

    override val backgroundScheduler: Scheduler = Schedulers.io()

    override val foregroundScheduler: Scheduler = AndroidSchedulers.mainThread()

  }
}
