/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.bootstrap.version

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.bus.EventBus
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber

class VersionCheckViewModel internal constructor(
  private val currentVersion: Int,
  private val packageName: String,
  private val interactor: VersionCheckInteractor,
  private val versionCheckBeginBus: EventBus<VersionEvents.Begin>,
  private val versionCheckFound: EventBus<VersionEvents.UpdateFound>,
  private val versionCheckError: EventBus<VersionEvents.UpdateError>,
  private val foregroundScheduler: Scheduler,
  private val backgroundScheduler: Scheduler
) {

  @CheckResult
  fun onCheckingForUpdates(onCheckBegin: (forced: Boolean) -> Unit): Disposable {
    return versionCheckBeginBus.listen()
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .subscribe { onCheckBegin(it.forced) }
  }

  @CheckResult
  fun onUpdateFound(onFound: (currentVersion: Int, newVersion: Int) -> Unit): Disposable {
    return versionCheckFound.listen()
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .subscribe { onFound(it.currentVersion, it.newVersion) }
  }

  @CheckResult
  fun onUpdateError(onError: (error: Throwable) -> Unit): Disposable {
    return versionCheckError.listen()
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .subscribe { onError(it.error) }
  }

  fun publishCheckingForUpdatesEvent(forced: Boolean) {
    versionCheckBeginBus.publish(VersionEvents.Begin(forced))
  }

  fun publishUpdateFoundEvent(newVersion: Int) {
    versionCheckFound.publish(VersionEvents.UpdateFound(currentVersion, newVersion))
  }

  fun publishUpdateErrorEvent(error: Throwable) {
    versionCheckError.publish(VersionEvents.UpdateError(error))
  }

  @CheckResult
  fun checkForUpdates(
    force: Boolean,
    onCheckBegin: (forced: Boolean) -> Unit,
    onCheckSuccess: (newVersion: Int) -> Unit,
    onCheckError: (error: Throwable) -> Unit,
    onCheckComplete: () -> Unit
  ): Disposable {
    return interactor.checkVersion(force, packageName)
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .doOnSubscribe { onCheckBegin(force) }
        .doAfterTerminate { onCheckComplete() }
        .subscribe({ onCheckSuccess(it) }, {
          Timber.e(it, "Error checking for latest version")
          onCheckError(it)
        })
  }
}
