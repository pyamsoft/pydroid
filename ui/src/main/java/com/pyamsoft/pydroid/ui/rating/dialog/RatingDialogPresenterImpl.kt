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

package com.pyamsoft.pydroid.ui.rating.dialog

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.arch.BasePresenter
import com.pyamsoft.pydroid.ui.arch.destroy

internal class RatingDialogPresenterImpl internal constructor(
  private val interactor: RatingInteractor,
  private val schedulerProvider: SchedulerProvider,
  owner: LifecycleOwner,
  bus: EventBus<RatingSavedEvent>
) : BasePresenter<RatingSavedEvent, RatingDialogPresenter.Callback>(owner, bus),
    RatingDialogPresenter, RatingControlsView.Callback {

  private var saveDisposable by singleDisposable()

  override fun onBind() {
    listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it.rateApplication) {
            true -> callback.onVisitApplicationPageToRate(it.packageName)
            false -> callback.onDidNotRate()
          }
        }
        .destroy(owner)
  }

  override fun onUnbind() {
    saveDisposable.tryDispose()
  }

  override fun onRateApplicationClicked(link: String) {
    save(link)
  }

  override fun onNotRatingApplication() {
    save("")
  }

  private fun save(link: String) {
    saveDisposable = interactor.saveRating()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe { publish(RatingSavedEvent(link.isNotBlank(), link)) }
  }

}