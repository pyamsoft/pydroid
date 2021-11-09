/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.version

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class VersionCheckViewModel
internal constructor(private val interactor: VersionInteractor) :
    UiViewModel<VersionCheckViewState, VersionCheckControllerEvent>(
        initialState =
            VersionCheckViewState(
                isLoading = false,
                versionCheckError = null,
                navigationError = null,
            )) {

  private val checkUpdateRunner =
      highlander<ResultWrapper<UpdateResult>, Boolean> { force ->
        interactor.checkVersion(force).map { UpdateResult(force, it) }
      }

  init {
    viewModelScope.launch(context = Dispatchers.Default) {
      interactor.watchForDownloadComplete {
        Logger.d("App update download ready!")
        publish(VersionCheckControllerEvent.UpgradeReady)
      }
    }
  }

  private fun handleVersionCheckError(throwable: Throwable) {
    setState { copy(versionCheckError = throwable) }
  }

  internal fun handleClearError() {
    setState { copy(versionCheckError = null) }
  }

  internal fun handleHideNavigation() {
    setState { copy(navigationError = null) }
  }

  internal fun handleNavigationSuccess() {
    handleHideNavigation()
  }

  internal fun handleNavigationFailed(error: Throwable) {
    setState { copy(navigationError = error) }
  }

  internal fun handleCheckForUpdates(force: Boolean) {
    Logger.d("Begin check for updates")
    viewModelScope.setState(
        stateChange = { copy(isLoading = true) },
        andThen = {
          checkUpdateRunner
              .call(force)
              .onSuccess {
                publish(VersionCheckControllerEvent.LaunchUpdate(it.isFallbackEnabled, it.launcher))
              }
              .onFailure { Logger.e(it, "Error checking for latest version") }
              .onFailure { handleVersionCheckError(it) }
              .onFinally { setState { copy(isLoading = false) } }
        })
  }

  internal data class UpdateResult
  internal constructor(val isFallbackEnabled: Boolean, val launcher: AppUpdateLauncher)
}
