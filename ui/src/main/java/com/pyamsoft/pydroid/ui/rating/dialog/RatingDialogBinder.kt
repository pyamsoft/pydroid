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

import com.pyamsoft.pydroid.arch.UiBinder
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose

internal class RatingDialogBinder internal constructor(
  private val interactor: RatingInteractor,
  private val schedulerProvider: SchedulerProvider
) : UiBinder<RatingDialogBinder.Callback>(),
    RatingControlsView.Callback {

  private var saveDisposable by singleDisposable()

  override fun onBind() {
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
        .subscribe {
          val rate = link.isNotBlank()
          if (rate) {
            callback.handleVisitApplicationPageToRate(link)
          } else {
            callback.handleDidNotRate()
          }
        }
  }

  interface Callback : UiBinder.Callback {

    fun handleVisitApplicationPageToRate(packageName: String)

    fun handleDidNotRate()
  }

}
