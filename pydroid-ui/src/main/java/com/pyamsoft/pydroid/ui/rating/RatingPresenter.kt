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

package com.pyamsoft.pydroid.ui.rating

import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import com.pyamsoft.pydroid.ui.rating.RatingPresenter.View
import io.reactivex.Scheduler
import timber.log.Timber

internal class RatingPresenter internal constructor(
  private val currentVersion: Int,
  private val interactor: RatingInteractor,
  computationScheduler: Scheduler,
  ioScheduler: Scheduler,
  mainThreadScheduler: Scheduler
) : SchedulerPresenter<View>(computationScheduler, ioScheduler, mainThreadScheduler) {

  internal fun loadRatingDialog(force: Boolean) {
    dispose {
      interactor.needsToViewRating(force, currentVersion)
          .subscribeOn(
              ioScheduler
          )
          .observeOn(mainThreadScheduler)
          .subscribe({
            if (it) {
              view?.onShowRatingDialog()
            }
          }, {
            Timber.e(it, "on error loading rating dialog")
            view?.onRatingDialogLoadError(it)
          })
    }
  }

  internal interface View : RatingLoadCallback

  internal interface RatingLoadCallback {

    fun onShowRatingDialog()

    fun onRatingDialogLoadError(throwable: Throwable)
  }
}
