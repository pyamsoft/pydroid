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

package com.pyamsoft.pydroid.bootstrap.version.play

import android.content.Context
import androidx.annotation.CheckResult
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdater
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.util.isDebugMode
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

internal class PlayStoreAppUpdater
internal constructor(
    enforcer: ThreadEnforcer,
    context: Context,
    version: Int,
    isFakeUpgradeAvailable: Boolean,
) : AppUpdater {

  private val manager by lazy {
    enforcer.assertOffMainThread()

    if (context.isDebugMode()) {
      FakeAppUpdateManager(context.applicationContext).apply {
        if (isFakeUpgradeAvailable) {
          setUpdateAvailable(version + 1)
        }
      }
    } else {
      AppUpdateManagerFactory.create(context.applicationContext)
    }
  }

  override suspend fun complete() =
      withContext(context = Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
          Logger.d { "Now completing update..." }
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

  override suspend fun watchDownloadStatus(
      onDownloadProgress: (Float) -> Unit,
      onDownloadCompleted: () -> Unit
  ) =
      withContext(context = Dispatchers.Default) {
        suspendCancellableCoroutine<Unit> { continuation ->
          val listener =
              createStatusListener(
                  onDownloadProgress = onDownloadProgress,
                  onDownloadCompleted = onDownloadCompleted,
              )

          Logger.d { "Listen for install status" }
          manager.registerListener(listener)

          continuation.invokeOnCancellation {
            Logger.d { "Stop listening for install status" }
            manager.unregisterListener(listener)
          }
        }
      }

  override suspend fun checkForUpdate(): AppUpdateLauncher =
      withContext(context = Dispatchers.IO) {
        if (manager is FakeAppUpdateManager) {
          Logger.d { "In debug mode we fake a delay to mimic real world network turnaround time." }
          delay(2000L)
        }

        return@withContext suspendCancellableCoroutine { continuation ->
          manager.appUpdateInfo
              .addOnCanceledListener {
                Logger.w { "In-App update cancelled" }
                continuation.cancel()
              }
              .addOnFailureListener { error ->
                Logger.e(error) { "Failed to resolve app update info task" }
                continuation.resume(AppUpdateLauncher.empty())
              }
              .addOnSuccessListener { info ->
                Logger.d { "App Update info received: $info" }
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                  val updateType = AppUpdateType.FLEXIBLE
                  if (info.isUpdateTypeAllowed(updateType)) {
                    Logger.d { "Update is available and flexible" }
                    continuation.resume(PlayStoreAppUpdateLauncher(manager, info, updateType))
                    return@addOnSuccessListener
                  }
                }

                Logger.d { "Update is not available" }
                continuation.resume(AppUpdateLauncher.empty())
              }
        }
      }

  companion object {

    @CheckResult
    private inline fun createStatusListener(
        crossinline onDownloadProgress: (Float) -> Unit,
        crossinline onDownloadCompleted: () -> Unit
    ): InstallStateUpdatedListener {
      return InstallStateUpdatedListener { state ->
        val status = state.installStatus()
        Logger.d { "Install state changed: $status" }
        if (status == InstallStatus.DOWNLOADING) {
          Logger.d { "Download in progress" }
          val bytesDownloaded = state.bytesDownloaded()
          val totalBytes = state.totalBytesToDownload()
          val progress = (bytesDownloaded / totalBytes.toFloat())
          Logger.d { "Download status: $bytesDownloaded / $totalBytes => $progress" }
          onDownloadProgress(progress)
        } else if (status == InstallStatus.DOWNLOADED) {
          Logger.d { "Download completed!" }
          onDownloadCompleted()
        }
      }
    }
  }
}
