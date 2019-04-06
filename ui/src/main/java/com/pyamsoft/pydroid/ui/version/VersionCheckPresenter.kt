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

import com.pyamsoft.pydroid.arch.Presenter
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter.VersionState
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter.VersionState.Loading
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter.VersionState.UpgradePayload
import com.pyamsoft.pydroid.ui.version.VersionCheckState.Begin
import com.pyamsoft.pydroid.ui.version.VersionCheckState.Complete
import com.pyamsoft.pydroid.ui.version.VersionCheckState.Error
import com.pyamsoft.pydroid.ui.version.VersionCheckState.Found
import timber.log.Timber

internal class VersionCheckPresenter internal constructor(
  private val interactor: VersionCheckInteractor,
  private val schedulerProvider: SchedulerProvider,
  private val bus: EventBus<VersionCheckState>
) : Presenter<VersionState, VersionCheckPresenter.Callback>() {

  private var checkUpdatesDisposable by singleDisposable()

  override fun initialState(): VersionState {
    return VersionState(isLoading = null, throwable = null, upgrade = null)
  }

  override fun onBind() {
    listenForVersionCheckEvents()
    checkForUpdates(false)
  }

  override fun onUnbind() {
    checkUpdatesDisposable.tryDispose()
  }

  private fun listenForVersionCheckEvents() {
    bus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Begin -> handleVersionCheckBegin(it.forced)
            is Found -> handleVersionCheckFound(it.currentVersion, it.newVersion)
            is Error -> handleVersionCheckError(it.throwable)
            is Complete -> handleVersionCheckComplete()
          }
        }
        .destroy()
  }

  private fun handleVersionCheckBegin(forced: Boolean) {
    setState {
      copy(isLoading = Loading(forced))
    }
  }

  private fun handleVersionCheckFound(
    currentVersion: Int,
    newVersion: Int
  ) {
    setState {
      copy(upgrade = UpgradePayload(currentVersion, newVersion), throwable = null)
    }
  }

  private fun handleVersionCheckError(throwable: Throwable) {
    setState {
      copy(upgrade = null, throwable = throwable)
    }
  }

  private fun handleVersionCheckComplete() {
    setState {
      copy(isLoading = null)
    }
  }

  fun checkForUpdates(force: Boolean) {
    checkUpdatesDisposable = interactor.checkVersion(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .doOnSubscribe { bus.publish(VersionCheckState.Begin(force)) }
        .doAfterTerminate { bus.publish(VersionCheckState.Complete) }
        .subscribe({ bus.publish(VersionCheckState.Found(it.currentVersion, it.newVersion)) }, {
          Timber.e(it, "Error checking for latest version")
          bus.publish(VersionCheckState.Error(it))
        })
  }

  data class VersionState(
    val isLoading: Loading?,
    val throwable: Throwable?,
    val upgrade: UpgradePayload?
  ) {

    data class Loading(val forced: Boolean)

    data class UpgradePayload(
      val currentVersion: Int,
      val newVersion: Int
    )
  }

  interface Callback : com.pyamsoft.pydroid.arch.Presenter.Callback<VersionState>
}
