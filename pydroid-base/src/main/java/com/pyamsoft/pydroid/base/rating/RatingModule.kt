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

package com.pyamsoft.pydroid.base.rating

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.bus.Publisher
import com.pyamsoft.pydroid.bus.RxBus

class RatingModule(preferences: RatingPreferences) {

  private val errorBus: EventBus<Throwable> = RxBus.create()
  private val ratingBus: EventBus<Unit> = RxBus.create()
  private val saveBus: EventBus<Boolean> = RxBus.create()
  private val interactor: RatingInteractor = RatingInteractorImpl(preferences)

  @CheckResult
  fun getPublisher(): Publisher<Throwable> = errorBus

  @CheckResult
  fun getPresenter(version: Int): RatingPresenter {
    return RatingPresenter(version, interactor, errorBus, ratingBus)
  }

  @CheckResult
  fun getSavePresenter(version: Int): RatingSavePresenter {
    return RatingSavePresenter(version, interactor, saveBus)
  }
}
