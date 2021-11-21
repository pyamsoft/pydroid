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

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class VersionCheckViewModeler
internal constructor(
    private val state: MutableVersionCheckViewState,
    private val interactor: VersionInteractor,
) : AbstractViewModeler<VersionCheckViewState>(state) {

  private val checkUpdateRunner =
      highlander<ResultWrapper<UpdateResult>, Boolean> { force ->
        interactor.checkVersion(force).map { UpdateResult(force, it) }
      }

  internal fun bind(
      scope: CoroutineScope,
      onUpgradeReady: () -> Unit,
  ) {
    scope.launch(context = Dispatchers.Main) {
      interactor.watchForDownloadComplete {
        Logger.d("App update download ready!")
        onUpgradeReady()
      }
    }
  }

  internal fun handleClearError() {
    state.versionCheckError = null
  }

  internal fun handleHideNavigation() {
    state.navigationError = null
  }

  internal fun handleNavigationFailed(error: Throwable) {
    state.navigationError = error
  }

  internal fun handleCheckForUpdates(
      scope: CoroutineScope,
      force: Boolean,
      onLaunchUpdate: (Boolean, AppUpdateLauncher) -> Unit,
  ) {
    Logger.d("Begin check for updates")
    scope.launch(context = Dispatchers.Main) {
      state.isLoading = true

      checkUpdateRunner
          .call(force)
          .onSuccess { onLaunchUpdate(it.isFallbackEnabled, it.launcher) }
          .onFailure { Logger.e(it, "Error checking for latest version") }
          .onFailure { state.versionCheckError = it }
          .onFinally { state.isLoading = false }
    }
  }

  private data class UpdateResult(
      val isFallbackEnabled: Boolean,
      val launcher: AppUpdateLauncher,
  )
}
