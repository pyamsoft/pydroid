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

package com.pyamsoft.pydroid.bootstrap.version

import androidx.annotation.CheckResult
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdater
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.util.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

internal abstract class AbstractAppUpdater<T : AppUpdateManager>
protected constructor(
    enforcer: ThreadEnforcer,
    resolveAppUpdateManager: () -> T,
) : AppUpdater {

  protected val manager: T by lazy {
    enforcer.assertOffMainThread()
    resolveAppUpdateManager()
  }

  final override suspend fun completeUpgrade() =
      withContext(context = Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
          manager
              .completeUpdate()
              .addOnCanceledListener {
                Logger.w { "UPDATE CANCELLED" }
                continuation.cancel()
              }
              .addOnFailureListener { err ->
                Logger.e(err) { "UPDATE ERROR" }
                continuation.resumeWithException(err)
              }
              .addOnSuccessListener {
                Logger.d { "UPDATE COMPLETE" }
                continuation.resume(Unit)
              }
        }
      }

  final override suspend fun watchDownloadStatus(
      onDownloadProgress: (Float) -> Unit,
      onDownloadCompleted: () -> Unit,
      onDownloadCancelled: () -> Unit,
      onDownloadFailed: () -> Unit
  ) =
      withContext(context = Dispatchers.Default) {
        suspendCancellableCoroutine<Unit> { continuation ->
          val listener =
              createStatusListener(
                  onDownloadProgress = onDownloadProgress,
                  onDownloadCompleted = onDownloadCompleted,
                  onDownloadCancelled = onDownloadCancelled,
                  onDownloadFailed = onDownloadFailed,
              )

          Logger.d { "Listen for install status" }
          manager.registerListener(listener)

          continuation.invokeOnCancellation {
            Logger.d { "Stop listening for install status" }
            manager.unregisterListener(listener)
          }
        }
      }

  final override suspend fun checkForUpdate(): AppUpdateLauncher =
      withContext(context = Dispatchers.IO) {
        onBeforeCheckForUpdate()

        return@withContext suspendCancellableCoroutine { continuation ->
          manager.appUpdateInfo
              .addOnCanceledListener {
                Logger.w { "In-app update fetch was cancelled" }
                continuation.cancel()
              }
              .addOnFailureListener { error ->
                Logger.e(error) { "Failed to resolve app update info task" }
                continuation.resume(
                    AppUpdateLauncher.empty(
                        status = AppUpdateResultStatus.IN_APP_UPDATE_FAILED,
                    ),
                )
              }
              .addOnSuccessListener { info ->
                Logger.d { "In-App Update info received: $info" }
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                  val updateType = AppUpdateType.FLEXIBLE
                  if (info.isUpdateTypeAllowed(updateType)) {
                    Logger.d { "In-App Update is available and flexible" }
                    continuation.resume(
                        createAppUpdateLauncher(
                            info,
                            updateType,
                        ),
                    )
                    return@addOnSuccessListener
                  }
                }

                Logger.d { "In-App Update is not available" }
                continuation.resume(AppUpdateLauncher.empty())
              }
        }
      }

  @CheckResult
  protected abstract fun createAppUpdateLauncher(
      info: AppUpdateInfo,
      @AppUpdateType updateType: Int
  ): AppUpdateLauncher

  protected open suspend fun onBeforeCheckForUpdate() {}

  companion object {

    @CheckResult
    private inline fun createStatusListener(
        crossinline onDownloadProgress: (Float) -> Unit,
        crossinline onDownloadCompleted: () -> Unit,
        crossinline onDownloadCancelled: () -> Unit,
        crossinline onDownloadFailed: () -> Unit
    ): InstallStateUpdatedListener {
      return InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
          InstallStatus.DOWNLOADING -> {
            val bytesDownloaded = state.bytesDownloaded()
            val totalBytes = state.totalBytesToDownload()
            val progress = (bytesDownloaded / totalBytes.toFloat())
            Logger.d { "Download status: $bytesDownloaded / $totalBytes => $progress" }
            onDownloadProgress(progress)
          }

          InstallStatus.DOWNLOADED -> {
            onDownloadCompleted()
          }

          InstallStatus.CANCELED -> {
            onDownloadCancelled()
          }

          InstallStatus.FAILED -> {
            onDownloadFailed()
          }

          else -> {
            // Intentionally blank to silence lint
          }
        }
      }
    }
  }
}
