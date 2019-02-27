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

import com.pyamsoft.pydroid.arch.BasePresenter
import com.pyamsoft.pydroid.arch.destroy
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter.Callback
import com.pyamsoft.pydroid.ui.version.VersionCheckState.Begin
import com.pyamsoft.pydroid.ui.version.VersionCheckState.Complete
import com.pyamsoft.pydroid.ui.version.VersionCheckState.Error
import com.pyamsoft.pydroid.ui.version.VersionCheckState.Found
import timber.log.Timber

internal class VersionCheckPresenterImpl internal constructor(
  private val interactor: VersionCheckInteractor,
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<VersionCheckState>
) : BasePresenter<VersionCheckState, Callback>(bus),
    VersionCheckPresenter {

  private var checkUpdatesDisposable by singleDisposable()

  override fun onBind() {
    listenForVersionCheckEvents()
    checkForUpdates(false)
  }

  private fun listenForVersionCheckEvents() {
    listen().subscribe {
      return@subscribe when (it) {
        is Begin -> callback.onVersionCheckBegin(it.forced)
        is Found -> callback.onVersionCheckFound(it.currentVersion, it.newVersion)
        is Error -> callback.onVersionCheckError(it.throwable)
        is Complete -> callback.onVersionCheckComplete()
      }
    }
        .destroy(owner)
  }

  override fun checkForUpdates(force: Boolean) {
    checkUpdatesDisposable = interactor.checkVersion(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .doOnSubscribe { publish(VersionCheckState.Begin(force)) }
        .doAfterTerminate { publish(VersionCheckState.Complete) }
        .subscribe({ publish(VersionCheckState.Found(it.currentVersion, it.newVersion)) }, {
          Timber.e(it, "Error checking for latest version")
          publish(VersionCheckState.Error(it))
        })
  }

  override fun onUnbind() {
    checkUpdatesDisposable.tryDispose()
  }
}
