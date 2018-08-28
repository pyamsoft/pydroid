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

package com.pyamsoft.pydroid.bootstrap.rating

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.DataBus
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.threads.Enforcer

class RatingModule(
  preferences: RatingPreferences,
  enforcer: Enforcer,
  private val schedulerProvider: SchedulerProvider
) {

  private val errorBus: EventBus<Throwable> = RxBus.create()
  private val interactor: RatingInteractor = RatingInteractorImpl(enforcer, preferences)
  private val ratingBus = DataBus<Unit>()
  private val ratingSaveBus = DataBus<Boolean>()

  @CheckResult
  fun getPublisher(): Publisher<Throwable> = errorBus

  @CheckResult
  fun getViewModel(version: Int): RatingViewModel {
    return RatingViewModel(
        ratingBus,
        ratingSaveBus,
        version,
        interactor,
        errorBus,
        schedulerProvider.foregroundScheduler,
        schedulerProvider.backgroundScheduler
    )
  }
}
