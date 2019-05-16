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

package com.pyamsoft.pydroid.ui.rating

import com.pyamsoft.pydroid.arch.impl.BaseUiViewModel
import com.pyamsoft.pydroid.arch.impl.UnitViewEvent
import com.pyamsoft.pydroid.arch.impl.UnitViewState
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.rating.RatingControllerEvent.ShowDialog

internal class RatingViewModel internal constructor(
  private val interactor: RatingInteractor,
  private val schedulerProvider: SchedulerProvider
) : BaseUiViewModel<UnitViewState, UnitViewEvent, RatingControllerEvent>(
    initialState = UnitViewState
) {

  private var loadDisposable by singleDisposable()

  override fun handleViewEvent(event: UnitViewEvent) {
  }

  internal fun load(force: Boolean) {
    loadDisposable = interactor.needsToViewRating(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .doAfterTerminate { loadDisposable.tryDispose() }
        .subscribe { publish(ShowDialog) }
  }

}
