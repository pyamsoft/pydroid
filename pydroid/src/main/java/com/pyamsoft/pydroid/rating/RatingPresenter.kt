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

import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber

class RatingPresenter internal constructor(private val interactor: RatingInteractor,
    observeScheduler: Scheduler, subscribeScheduler: Scheduler) : SchedulerPresenter(
    observeScheduler, subscribeScheduler) {

  fun loadRatingDialog(currentVersion: Int, force: Boolean, callback: RatingCallback) {
    disposeOnDestroy(interactor.needsToViewRating(currentVersion, force).subscribeOn(
        subscribeScheduler).observeOn(observeScheduler).doAfterTerminate(
        { callback.onLoadComplete() }).subscribe({
      if (it) {
        callback.onShowRatingDialog()
      }
    }) {
      Timber.e(it, "on error loading rating dialog")
      callback.onRatingDialogLoadError(it)
    })
  }

  fun saveRating(versionCode: Int, callback: SaveCallback) {
    disposeOnDestroy(interactor.saveRating(versionCode).subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).subscribe({
      Timber.d("Saved current version code: %d", versionCode)
      callback.onRatingSaved()
    }) {
      Timber.e(it, "on error loading rating dialog")
      callback.onRatingDialogSaveError(it)
    })
  }

  interface RatingCallback {

    fun onShowRatingDialog()

    fun onRatingDialogLoadError(throwable: Throwable)

    fun onLoadComplete()
  }

  interface SaveCallback {

    fun onRatingSaved()

    fun onRatingDialogSaveError(throwable: Throwable)
  }
}
