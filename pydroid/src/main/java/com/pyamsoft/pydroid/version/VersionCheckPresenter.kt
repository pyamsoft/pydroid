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

class VersionCheckPresenter(private val interactor: VersionCheckInteractor,
    observeScheduler: Scheduler, subscribeScheduler: Scheduler) : SchedulerPresenter(
    observeScheduler, subscribeScheduler) {

  fun forceCheckForUpdates(packageName: String, currentVersionCode: Int,
      onUpdatedVersionFound: (current: Int, updated: Int) -> Unit,
      onVersionCheckFinished: () -> Unit) {
    checkForUpdates(packageName, currentVersionCode, true, onUpdatedVersionFound,
        onVersionCheckFinished)
  }

  fun checkForUpdates(packageName: String, currentVersionCode: Int,
      onUpdatedVersionFound: (current: Int, updated: Int) -> Unit,
      onVersionCheckFinished: () -> Unit) {
    checkForUpdates(packageName, currentVersionCode, false, onUpdatedVersionFound,
        onVersionCheckFinished)
  }

  private fun checkForUpdates(packageName: String, currentVersionCode: Int, force: Boolean,
      onUpdatedVersionFound: (current: Int, updated: Int) -> Unit,
      onVersionCheckFinished: () -> Unit) {
    disposeOnStop {
      interactor.checkVersion(packageName, force).subscribeOn(backgroundScheduler).observeOn(
          foregroundScheduler).subscribe({
        Timber.i("Update check finished")
        Timber.i("Current version: %d", currentVersionCode)
        Timber.i("Latest version: %d", it)
        onVersionCheckFinished()
        if (currentVersionCode < it) {
          onUpdatedVersionFound(currentVersionCode, it)
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
}
