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

package com.pyamsoft.pydroid.ui.rating

import com.pyamsoft.pydroid.helper.DisposableHelper
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

internal class RatingPresenter(private val interactor: RatingInteractor,
    observeScheduler: Scheduler, subscribeScheduler: Scheduler) : SchedulerPresenter<Unit>(
    observeScheduler, subscribeScheduler) {

  private val composite = CompositeDisposable()

  override fun onStart(bound: Unit) {
    super.onStart(bound)
    throw IllegalStateException("RatingPresenter does not use start()")
  }

  override fun onStop() {
    super.onStop()
    throw IllegalStateException("RatingPresenter does not use stop()")
  }

  fun loadRatingDialog(currentVersion: Int, force: Boolean, onShowRatingDialog: () -> Unit,
      onRatingDialogLoadError: (Throwable) -> Unit) {
    DisposableHelper.add(composite) {
      interactor.needsToViewRating(currentVersion, force).subscribeOn(
          backgroundScheduler).observeOn(foregroundScheduler).subscribe({
        if (it) {
          onShowRatingDialog()
        }
      }, {
        Timber.e(it, "on error loading rating dialog")
        onRatingDialogLoadError(it)
      })
    }
  }

  fun saveRating(versionCode: Int, onRatingSaved: () -> Unit,
      onRatingDialogSaveError: (Throwable) -> Unit) {
    DisposableHelper.add(composite) {
      interactor.saveRating(versionCode).subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).subscribe({
        Timber.d("Saved current version code: %d", versionCode)
        onRatingSaved()
      }, {
        Timber.e(it, "Error saving rating dialog")
        onRatingDialogSaveError(it)
      })
    }
  }

  fun clear() {
    composite.clear()
  }
}
