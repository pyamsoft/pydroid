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

package com.pyamsoft.pydroid.version

import com.pyamsoft.pydroid.presenter.SchedulerPresenter
import io.reactivex.Scheduler
import retrofit2.HttpException
import timber.log.Timber

class VersionCheckPresenter constructor(private val interactor: VersionCheckInteractor,
    observeScheduler: Scheduler, subscribeScheduler: Scheduler) : SchedulerPresenter(
    observeScheduler, subscribeScheduler) {

  fun forceCheckForUpdates(packageName: String, currentVersionCode: Int,
      callback: UpdateCheckCallback) {
    checkForUpdates(packageName, currentVersionCode, true, callback)
  }

  fun checkForUpdates(packageName: String, currentVersionCode: Int, callback: UpdateCheckCallback) {
    checkForUpdates(packageName, currentVersionCode, false, callback)
  }

  private fun checkForUpdates(packageName: String, currentVersionCode: Int, force: Boolean,
      callback: UpdateCheckCallback) {
    disposeOnStop(
        interactor.checkVersion(packageName, force).subscribeOn(subscribeScheduler).observeOn(
            observeScheduler).subscribe({
          Timber.i("Update check finished")
          Timber.i("Current version: %d", currentVersionCode)
          Timber.i("Latest version: %d", it)
          callback.onVersionCheckFinished()
          if (currentVersionCode < it) {
            callback.onUpdatedVersionFound(currentVersionCode, it)
          }
        }) {
          if (it is HttpException) {
            Timber.e(it, "Network Failure: %d", it.code())
          } else {
            Timber.e(it, "onError")
          }
        })
  }

  interface UpdateCheckCallback {

    fun onVersionCheckFinished()

    fun onUpdatedVersionFound(oldVersionCode: Int, updatedVersionCode: Int)
  }
}
