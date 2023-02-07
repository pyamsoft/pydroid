/*
 * Copyright 2022 Peter Kenji Yamanaka
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
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class VersionCheckViewModeler
internal constructor(
    override val state: MutableVersionCheckViewState,
    private val interactor: VersionInteractor,
    private val interactorCache: VersionInteractor.Cache,
) : AbstractViewModeler<VersionCheckViewState>(state) {

  private val checkUpdateRunner =
      highlander<ResultWrapper<AppUpdateLauncher>, Boolean> { force ->
        if (force) {
          interactorCache.invalidateVersion()
        }

        return@highlander interactor.checkVersion()
      }

  internal fun bind(
      scope: CoroutineScope,
      onUpgradeReady: () -> Unit,
  ) {
    val s = state
    scope.launch(context = Dispatchers.Main) {
      interactor.watchDownloadStatus(
          onDownloadProgress = { percent ->
            if (!s.isUpdateReadyToInstall.value) {
              Logger.d("Update progress: $percent")
              s.updateProgressPercent.value = percent
            } else {
              Logger.w("Download marks progress, but update is ready to install: $percent")
            }
          },
          onDownloadCompleted = {
            Logger.d("App update download ready!")
            s.isUpdateReadyToInstall.value = true
            s.updateProgressPercent.value = 0F
            onUpgradeReady()
          },
      )
    }
  }

  internal fun handleCheckForUpdates(
      scope: CoroutineScope,
      force: Boolean,
      onLaunchUpdate: (AppUpdateLauncher) -> Unit,
  ) {
    val s = state

    if (s.isUpdateReadyToInstall.value) {
      Logger.d("Update is already ready to install, do not check for update again")
      return
    }

    if (s.isCheckingForUpdate.value == VersionCheckViewState.CheckingState.CHECKING) {
      Logger.d("We are already checking for an update.")
      return
    }

    Logger.d("Begin check for updates")
    s.isCheckingForUpdate.value = VersionCheckViewState.CheckingState.CHECKING
    scope.launch(context = Dispatchers.Main) {
      checkUpdateRunner
          .call(force)
          .onSuccess { Logger.d("Update data found as: $it") }
          .onSuccess { s.availableUpdateVersionCode.value = it.availableUpdateVersion() }
          .onSuccess(onLaunchUpdate)
          .onFailure { Logger.e(it, "Error checking for latest version") }
          .onFailure {
            s.availableUpdateVersionCode.value = AppUpdateLauncher.NO_VALID_UPDATE_VERSION
          }
          .onFinally { Logger.d("Done checking for updates") }
          .onFinally { s.isCheckingForUpdate.value = VersionCheckViewState.CheckingState.DONE }
    }
  }

  internal fun handleOpenDialog() {
    state.isUpgradeDialogShowing.value = true
  }

  internal fun handleCloseDialog() {
    state.isUpgradeDialogShowing.value = false
  }

  companion object {

    /** Request code for in-app updates Only bottom 16 bits. */
    internal const val RC_APP_UPDATE = 146
  }
}
