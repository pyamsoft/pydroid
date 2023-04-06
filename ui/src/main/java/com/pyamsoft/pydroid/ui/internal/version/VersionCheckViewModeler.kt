/*
 * Copyright 2023 pyamsoft
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

import androidx.annotation.CheckResult
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

  @CheckResult
  private suspend fun runCheckUpdate(force: Boolean): ResultWrapper<AppUpdateLauncher> {
    if (force) {
      interactorCache.invalidateVersion()
    }

    return interactor.checkVersion()
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
              s.updateProgressPercent.value = 1F
            }
          },
          onDownloadCompleted = {
            Logger.d("App update download ready!")
            s.isUpdateReadyToInstall.value = true
            onUpgradeReady()
          },
      )
    }
  }

  internal fun handleCheckForUpdates(
      scope: CoroutineScope,
      force: Boolean,
  ) {
    val s = state

    if (s.launcher.value != null) {
      Logger.d("Launcher is already available, do not check for update again")
      return
    }

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
      runCheckUpdate(force)
          .onSuccess { Logger.d("Update data found as: $it") }
          .onSuccess { s.launcher.value = it }
          .onFailure { Logger.e(it, "Error checking for latest version") }
          .onFailure { s.launcher.value = null }
          .onFinally { Logger.d("Done checking for updates") }
          .onFinally { s.isCheckingForUpdate.value = VersionCheckViewState.CheckingState.DONE }
    }
  }

  internal fun handleCompleteUpgrade(
      scope: CoroutineScope,
      onUpgradeCompleted: () -> Unit,
  ) {
    if (state.isUpgraded.value) {
      Logger.w("Already upgraded, do nothing")
      return
    }

    state.isUpgraded.value = true
    scope.launch(context = Dispatchers.Main) {
      Logger.d("Updating app, restart via update manager!")
      interactor.completeUpdate()
      onUpgradeCompleted()
    }
  }

  companion object {

    /** Request code for in-app updates Only bottom 16 bits. */
    internal const val RC_APP_UPDATE = 146
  }
}
