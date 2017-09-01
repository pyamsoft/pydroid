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

package com.pyamsoft.pydroid.about

import com.pyamsoft.pydroid.about.AboutLibrariesPresenter.LoadCallback
import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import timber.log.Timber

class AboutLibrariesPresenter internal constructor(private val interactor: AboutLibrariesInteractor,
    computationScheduler: Scheduler, ioScheduler: Scheduler,
    mainThreadScheduler: Scheduler) : SchedulerPresenter<LoadCallback>(computationScheduler,
    ioScheduler, mainThreadScheduler) {

  override fun onBind(v: LoadCallback) {
    super.onBind(v)
    loadLicenses(false, v::onLicenseLoaded, v::onAllLoaded)
  }

  private fun loadLicenses(force: Boolean, onLicenseLoaded: (AboutLibrariesModel) -> Unit,
      onAllLoaded: () -> Unit) {
    dispose {
      interactor.loadLicenses(force).subscribeOn(ioScheduler).observeOn(
          mainThreadScheduler).doAfterTerminate { onAllLoaded() }.subscribe({ onLicenseLoaded(it) },
          { Timber.e(it, "onError loading licenses") })
    }
  }

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
