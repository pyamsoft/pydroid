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

import com.pyamsoft.pydroid.arch.BasePresenter
import com.pyamsoft.pydroid.arch.destroy
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.rating.RatingPresenter.Callback

internal class RatingPresenterImpl internal constructor(
  private val interactor: RatingInteractor,
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<ShowRating>
) : BasePresenter<ShowRating, Callback>(bus), RatingPresenter {

  private var loadDisposable by singleDisposable()

  override fun onBind() {
    listenForDialogRequests()
    load(false)
  }

  override fun onUnbind() {
    loadDisposable.tryDispose()
  }

  private fun listenForDialogRequests() {
    listen().subscribe { callback.onShowRating() }
        .destroy(owner)
  }

  override fun load(force: Boolean) {
    loadDisposable = interactor.needsToViewRating(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe { publish(ShowRating) }
  }

}
