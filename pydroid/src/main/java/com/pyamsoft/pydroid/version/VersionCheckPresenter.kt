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

package com.pyamsoft.pydroid.version

import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import com.pyamsoft.pydroid.version.VersionCheckPresenter.View
import io.reactivex.Scheduler
import retrofit2.HttpException
import timber.log.Timber

class VersionCheckPresenter internal constructor(private val packageName: String,
    private val currentVersionCode: Int,
    private val interactor: VersionCheckInteractor,
    computationScheduler: Scheduler, ioScheduler: Scheduler,
    mainThreadScheduler: Scheduler) : SchedulerPresenter<View>(
    computationScheduler, ioScheduler, mainThreadScheduler) {

  fun checkForUpdates(force: Boolean) {
    dispose {
      interactor.checkVersion(packageName, force).subscribeOn(ioScheduler).observeOn(
          mainThreadScheduler).subscribe({ updated ->
        Timber.i("Update check finished")
        Timber.i("Current version: %d", currentVersionCode)
        Timber.i("Latest version: %d", updated)
        if (currentVersionCode < updated) {
          withView<UpdateCallback> { it.onUpdatedVersionFound(currentVersionCode, updated) }
        }
      }, {
        if (it is HttpException) {
          Timber.e(it, "Network Failure: %d", it.code())
        } else {
          Timber.e(it, "onError")
        }
      })
    }
  }

  interface View : UpdateCallback

  interface UpdateCallback {

    fun onUpdatedVersionFound(current: Int, updated: Int)
  }
}
