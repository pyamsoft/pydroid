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

import com.pyamsoft.pydroid.arch.UiState
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.rating.RatingViewModel.RatingState
import javax.inject.Inject

internal class RatingViewModel @Inject internal constructor(
  private val interactor: RatingInteractor,
  private val schedulerProvider: SchedulerProvider,
  private val bus: EventBus<ShowRating>
) : UiViewModel<RatingState>(
    initialState = RatingState(showRating = false)
) {

  private var loadDisposable by singleDisposable()

  override fun onBind() {
    listenForDialogRequests()
    load(false)
  }

  override fun onUnbind() {
    loadDisposable.tryDispose()
  }

  private fun listenForDialogRequests() {
    bus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe { handleShowRating() }
        .destroy()
  }

  private fun handleShowRating() {
    setUniqueState(true, old = { it.showRating }) { state, value -> state.copy(showRating = value) }
  }

  fun load(force: Boolean) {
    loadDisposable = interactor.needsToViewRating(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe { bus.publish(ShowRating) }
  }

  data class RatingState(val showRating: Boolean) : UiState

}
