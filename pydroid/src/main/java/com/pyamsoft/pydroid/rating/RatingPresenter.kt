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

class RatingPresenter(private val interactor: RatingInteractor, observeScheduler: Scheduler,
    subscribeScheduler: Scheduler) : SchedulerPresenter(observeScheduler, subscribeScheduler) {

  fun loadRatingDialog(currentVersion: Int, force: Boolean, onShowRatingDialog: () -> Unit,
      onRatingDialogLoadError: (Throwable) -> Unit, onLoadComplete: () -> Unit) {
    disposeOnDestroy(interactor.needsToViewRating(currentVersion, force).subscribeOn(
        subscribeScheduler).observeOn(observeScheduler).doAfterTerminate(
        { onLoadComplete() }).subscribe({
      if (it) {
        onShowRatingDialog()
      }
    }, {
      Timber.e(it, "on error loading rating dialog")
      onRatingDialogLoadError(it)
    }))
  }

  fun saveRating(versionCode: Int, onRatingSaved: () -> Unit,
      onRatingDialogSaveError: (Throwable) -> Unit) {
    disposeOnDestroy(interactor.saveRating(versionCode).subscribeOn(subscribeScheduler).observeOn(
        observeScheduler).subscribe({
      Timber.d("Saved current version code: %d", versionCode)
      onRatingSaved()
    }, {
      Timber.e(it, "Error saving rating dialog")
      onRatingDialogSaveError(it)
    }))
  }
}
