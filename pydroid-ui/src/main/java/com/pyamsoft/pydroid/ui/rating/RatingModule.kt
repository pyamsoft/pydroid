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

package com.pyamsoft.pydroid.ui.rating

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.ui.RatingPreferences
import io.reactivex.Scheduler

internal class RatingModule internal constructor(module: PYDroidModule,
    preferences: RatingPreferences) {

  private val interactor: RatingInteractor
  private val computationScheduler: Scheduler = module.provideComputationScheduler()
  private val ioScheduler: Scheduler = module.provideIoScheduler()
  private val mainThreadScheduler: Scheduler = module.provideMainThreadScheduler()

  init {
    interactor = RatingInteractorImpl(preferences)
  }

  @CheckResult fun getPresenter(version: Int): RatingPresenter {
    return RatingPresenter(version, interactor, computationScheduler, ioScheduler,
        mainThreadScheduler)
  }

  @CheckResult fun getSavePresenter(version: Int): RatingSavePresenter {
    return RatingSavePresenter(version, interactor, computationScheduler, ioScheduler,
        mainThreadScheduler)
  }
}
