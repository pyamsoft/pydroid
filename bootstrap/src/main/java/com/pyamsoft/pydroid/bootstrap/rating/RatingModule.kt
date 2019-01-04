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

package com.pyamsoft.pydroid.bootstrap.rating

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.threads.Enforcer

class RatingModule(
  preferences: RatingPreferences,
  enforcer: Enforcer,
  private val currentVersion: Int,
  private val showRatingBus: EventBus<RatingEvents.ShowEvent>,
  private val showErrorRatingBus: EventBus<RatingEvents.ShowErrorEvent>,
  private val saveRatingErrorBus: EventBus<RatingEvents.SaveErrorEvent>,
  private val schedulerProvider: SchedulerProvider
) {

  private val interactor: RatingInteractor = RatingInteractorImpl(enforcer, preferences)

  @CheckResult
  fun getViewModel(): RatingViewModel {
    return RatingViewModel(
        interactor,
        currentVersion,
        showRatingBus,
        showErrorRatingBus,
        saveRatingErrorBus,
        schedulerProvider.foregroundScheduler,
        schedulerProvider.backgroundScheduler
    )
  }
}
