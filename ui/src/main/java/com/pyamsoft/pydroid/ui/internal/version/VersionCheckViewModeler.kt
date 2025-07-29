/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState
import com.pyamsoft.pydroid.ui.version.VersionCheckViewState.CheckingState
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.ifNotCancellation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class VersionCheckViewModeler
internal constructor(
    override val state: MutableVersionCheckViewState,
    private val interactor: VersionInteractor,
) : VersionCheckViewState by state, AbstractViewModeler<VersionCheckViewState>(state) {

  @CheckResult
  private fun canCheckForUpdates(force: Boolean): Boolean {
    if (force) {
      return true
    }

    val s = state
    if (s.isCheckingForUpdate.value is CheckingState.Checking) {
      Logger.d { "We are already checking for an update." }
      return false
    }

    if (s.isUpdateReadyToInstall.value) {
      Logger.d { "Update is already ready to install, do not check for update again" }
      return false
    }

    if (s.updateProgressPercent.value > 0) {
      Logger.d { "Update download is in progress, do not check for update again" }
      return false
    }

    if (s.launcher.value != null) {
      Logger.d { "Launcher is already available, do not check for update again" }
      return false
    }

    return true
  }

  private fun MutableVersionCheckViewState.handleInAppUpdateFailed() {
    updateProgressPercent.value = 0F
    isUpdateReadyToInstall.value = false
    updateError.value = UPDATE_FAILED_DOWNLOAD_ERROR
  }

  internal fun bind(scope: CoroutineScope) {
    val s = state
    scope.launch(context = Dispatchers.Default) {
      interactor.watchDownloadStatus(
          onDownloadProgress = { percent ->
            if (!s.isUpdateReadyToInstall.value) {
              s.updateProgressPercent.value = percent
            } else {
              Logger.w { "Download marks progress, but update is ready to install: $percent" }
              s.updateProgressPercent.value = 1F
            }
          },
          onDownloadCompleted = {
            Logger.d { "App update download ready!" }
            s.isUpdateReadyToInstall.value = true
          },
          onDownloadCancelled = {
            Logger.d { "App update download was canceled!" }

            // Clear the update values
            s.apply {
              updateProgressPercent.value = 0F
              isUpdateReadyToInstall.value = false
              updateError.value = null
            }

            // Since the user cancelled, we ALWAYS want to refresh the update flow
            handleCheckForUpdates(
                scope = scope,
                force = true,
            )
          },
          onDownloadFailed = {
            Logger.w { "App update download failed" }
            s.handleInAppUpdateFailed()
          },
      )
    }
  }

  internal fun handleCheckForUpdates(
      scope: CoroutineScope,
      force: Boolean,
      onLauncherReceived: (AppUpdateLauncher) -> Unit = {},
  ) {
    val s = state

    if (!canCheckForUpdates(force)) {
      return
    }

    Logger.d { "Begin check for updates" }
    scope.launch(context = Dispatchers.Default) {
      if (!canCheckForUpdates(force)) {
        return@launch
      }

      s.apply {
        // Start the upgrade check
        isCheckingForUpdate.value = CheckingState.Checking(force = force)

        // We are checking again, so reset progress
        isUpdateReadyToInstall.value = false
        updateProgressPercent.value = 0F
        updateError.value = null

        // Clear the launcher incase we have an old one hanging around
        launcher.value = null
      }

      interactor
          .checkVersion()
          .onSuccess { Logger.d { "Update data found as: $it" } }
          .onSuccess { s.launcher.value = it }
          .onSuccess(onLauncherReceived)
          .onFailure { Logger.e(it) { "Error checking for latest version" } }
          .onFailure { s.launcher.value = null }
          .onFinally {
            s.isCheckingForUpdate.value =
                CheckingState.Done(
                    force = force,
                )
          }
    }
  }

  internal fun handleCompleteUpgrade(
      scope: CoroutineScope,
      onUpgradeCompleted: () -> Unit,
  ) {
    if (state.isUpgraded.value) {
      Logger.w { "Already upgraded, do nothing" }
      return
    }

    state.isUpgraded.value = true
    scope.launch(context = Dispatchers.Default) {
      Logger.d { "Updating app, restart via update manager!" }
      try {
        interactor.completeUpdate()
      } catch (e: Throwable) {
        e.ifNotCancellation {
          Logger.e(e) { "Error during upgrade. Close application anyway to try again later." }
        }
      } finally {
        onUpgradeCompleted()
      }
    }
  }

  fun handleManualUpdateCheckComplete() {
    Logger.d { "Manual update check was completed. Mark force=false to stop visual updates" }
    state.isCheckingForUpdate.update { checking ->
      return@update when (checking) {
        is CheckingState.Checking -> checking.copy(force = false)
        is CheckingState.Done -> checking.copy(force = false)
        is CheckingState.None -> checking
      }
    }
  }

  fun handleInAppUpdateFailed() {
    Logger.d { "Unable to start in-app update download, process failed" }
    state.handleInAppUpdateFailed()
  }
}
