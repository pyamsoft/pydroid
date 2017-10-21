/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui.rating

import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import com.pyamsoft.pydroid.ui.rating.RatingSavePresenter.View
import io.reactivex.Scheduler
import timber.log.Timber

internal class RatingSavePresenter internal constructor(private val currentVersion: Int,
    private val interactor: RatingInteractor,
    computationScheduler: Scheduler, ioScheduler: Scheduler,
    mainThreadScheduler: Scheduler) : SchedulerPresenter<View>(
    computationScheduler, ioScheduler, mainThreadScheduler) {


  fun saveRating(accept: Boolean) {
    dispose {
      interactor.saveRating(currentVersion).subscribeOn(ioScheduler).observeOn(
          mainThreadScheduler).subscribe({
        Timber.d("Saved current version code: %d", currentVersion)
        view?.onRatingSaved(accept)
      }, {
        Timber.e(it, "Error saving rating dialog")
        view?.onRatingDialogSaveError(it)
      })
    }
  }

  interface View : SaveRatingCallback

  interface SaveRatingCallback {

    fun onRatingSaved(accept: Boolean)

    fun onRatingDialogSaveError(throwable: Throwable)
  }
}
