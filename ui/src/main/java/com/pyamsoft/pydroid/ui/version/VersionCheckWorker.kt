/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.version

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.arch.Worker
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.Loading
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.UpdateComplete
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.UpdateError
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.UpdateFound
import io.reactivex.disposables.Disposable
import timber.log.Timber

class VersionCheckWorker internal constructor(
  private val interactor: VersionCheckInteractor,
  private val versionStateCheckBus: EventBus<VersionStateEvent>,
  private val schedulerProvider: SchedulerProvider
) : Worker<VersionStateEvent> {

  @CheckResult
  fun onUpdateEvent(func: (payload: VersionStateEvent) -> Unit): Disposable {
    return versionStateCheckBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe(func)
  }

  @CheckResult
  fun checkForUpdates(force: Boolean): Disposable {
    return interactor.checkVersion(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .doOnSubscribe { versionStateCheckBus.publish(Loading(force)) }
        .doAfterTerminate { versionStateCheckBus.publish(UpdateComplete) }
        .subscribe(
            { versionStateCheckBus.publish(UpdateFound(it.currentVersion, it.newVersion)) }, {
          Timber.e(it, "Error checking for latest version")
          versionStateCheckBus.publish(UpdateError(it))
        })
  }
}
