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

package com.pyamsoft.pydroid.base.about

import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber

class AboutLibrariesPresenter internal constructor(
    private val interactor: AboutLibrariesInteractor,
    computationScheduler: Scheduler, ioScheduler: Scheduler,
    mainThreadScheduler: Scheduler
) : SchedulerPresenter<AboutLibrariesPresenter.View>(computationScheduler, ioScheduler, mainThreadScheduler) {

    override fun onCreate() {
        super.onCreate()
        loadLicenses(false)
    }

    private fun loadLicenses(force: Boolean) {
        dispose {
            interactor.loadLicenses(force).subscribeOn(ioScheduler).observeOn(mainThreadScheduler)
                .doAfterTerminate { view?.onAllLoaded() }
                .subscribe({ view?.onLicenseLoaded(it) },
                    { Timber.e(it, "onError loading licenses") })
        }
    }

    interface View : LoadCallback

    interface LoadCallback {

        /**
         * Called when a single license has finished loading. There are no guarantees about if the
         * license was loaded for the first time.
         */
        fun onLicenseLoaded(model: AboutLibrariesModel)

        /**
         * Called when all licenses are done loading
         */
        fun onAllLoaded()
    }
}
