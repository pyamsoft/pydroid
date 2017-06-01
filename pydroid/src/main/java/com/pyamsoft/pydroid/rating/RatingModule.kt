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

package com.pyamsoft.pydroid.rating

import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import com.pyamsoft.pydroid.PYDroidModule
import io.reactivex.Scheduler

@RestrictTo(RestrictTo.Scope.LIBRARY) class RatingModule(module: PYDroidModule) {

  private val interactor: RatingInteractor = RatingInteractor(module.provideRatingPreferences())
  private val obsScheduler: Scheduler = module.provideObsScheduler()
  private val subScheduler: Scheduler = module.provideSubScheduler()

  @CheckResult fun getPresenter(): RatingPresenter {
    return RatingPresenter(interactor, obsScheduler, subScheduler)
  }
}
