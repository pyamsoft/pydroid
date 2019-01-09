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
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.version.VersionEvents.Loading
import com.pyamsoft.pydroid.ui.version.VersionEvents.UpdateError
import com.pyamsoft.pydroid.ui.version.VersionEvents.UpdateFound
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import timber.log.Timber

class VersionCheckPresenter internal constructor(
  private val interactor: VersionCheckInteractor,
  private val versionCheckBus: EventBus<VersionEvents>,
  private val foregroundScheduler: Scheduler,
  private val backgroundScheduler: Scheduler
) {

  @CheckResult
  fun onUpdateEvent(func: (payload: VersionEvents) -> Unit): Disposable {
    return versionCheckBus.listen()
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .subscribe(func)
  }

  @CheckResult
  fun checkForUpdates(force: Boolean): Disposable {
    return interactor.checkVersion(force)
        .subscribeOn(backgroundScheduler)
        .observeOn(foregroundScheduler)
        .doOnSubscribe { versionCheckBus.publish(Loading(force)) }
        .subscribe({ versionCheckBus.publish(UpdateFound(it.currentVersion, it.newVersion)) }, {
          Timber.e(it, "Error checking for latest version")
          versionCheckBus.publish(UpdateError(it))
        })
  }
}
