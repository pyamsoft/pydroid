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

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.ui.RatingPreferences
import io.reactivex.Scheduler

internal class RatingModule internal constructor(module: PYDroidModule,
        preferences: RatingPreferences) {

    private val interactor: RatingInteractor
    private val computationScheduler: Scheduler = module.provideComputationScheduler()
    private val ioScheduler: Scheduler = module.provideIoScheduler()
    private val mainThreadScheduler: Scheduler = module.provideMainThreadScheduler()

    init {
        interactor = RatingInteractorImpl(preferences)
    }

    @CheckResult
    fun getPresenter(version: Int): RatingPresenter {
        return RatingPresenter(version, interactor, computationScheduler, ioScheduler,
                mainThreadScheduler)
    }

    @CheckResult
    fun getSavePresenter(version: Int): RatingSavePresenter {
        return RatingSavePresenter(version, interactor, computationScheduler, ioScheduler,
                mainThreadScheduler)
    }
}
