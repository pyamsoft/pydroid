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
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class VersionCheckViewModeler
internal constructor(
    private val state: MutableVersionCheckViewState,
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
    scope.launch(context = Dispatchers.Main) {
      interactor.watchDownloadStatus(
          onDownloadProgress = {},
          onDownloadCompleted = {
            Logger.d("App update download ready!")
            state.isUpdateReadyToInstall = true
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

    if (s.availableUpdateVersionCode != AppUpdateLauncher.NO_VALID_UPDATE_VERSION) {
      Logger.d("Update is already available, do not check for update again")
      return
    }

    if (s.isUpdateReadyToInstall) {
      Logger.d("Update is already ready to install, do not check for update again")
      return
    }

    if (s.isCheckingForUpdate) {
      Logger.d("We are already checking for an update.")
      return
    }

    Logger.d("Begin check for updates")
    s.isCheckingForUpdate = true
    scope.launch(context = Dispatchers.Main) {
      checkUpdateRunner
          .call(force)
          .onSuccess { Logger.d("Update data found as: $it") }
          .onSuccess { s.availableUpdateVersionCode = it.availableUpdateVersion() }
          .onSuccess(onLaunchUpdate)
          .onFailure { Logger.e(it, "Error checking for latest version") }
          .onFailure { s.availableUpdateVersionCode = AppUpdateLauncher.NO_VALID_UPDATE_VERSION }
          .onFinally { Logger.d("Done checking for updates") }
          .onFinally { s.isCheckingForUpdate = false }
    }
  }

  internal fun handleConfirmUpgrade(
      scope: CoroutineScope,
      onConfirmed: (Int) -> Unit,
  ) {
    val s = state

    val newVersionCode = s.availableUpdateVersionCode
    if (newVersionCode == AppUpdateLauncher.NO_VALID_UPDATE_VERSION) {
      Logger.w("No update version code is available, cannot confirm upgrade")
      return
    }

    if (!s.isUpdateReadyToInstall) {
      Logger.w("Update is not ready to install, cannot confirm upgrade!")
      return
    }

    scope.launch(context = Dispatchers.Main) { onConfirmed(newVersionCode) }
  }
}
