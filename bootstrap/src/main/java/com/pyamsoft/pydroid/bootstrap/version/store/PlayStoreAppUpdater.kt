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

package com.pyamsoft.pydroid.bootstrap.version.store

import android.content.Context
import androidx.annotation.CheckResult
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.AppUpdater
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

internal class PlayStoreAppUpdater
internal constructor(
    private val isFake: Boolean,
    context: Context,
    version: Int,
    isFakeUpgradeAvailable: Boolean
) : AppUpdater {

  private val manager by lazy {
    if (isFake) {
      FakeAppUpdateManager(context.applicationContext).apply {
        if (isFakeUpgradeAvailable) {
          setUpdateAvailable(version + 1)
        }
      }
    } else {
      AppUpdateManagerFactory.create(context.applicationContext)
    }
  }

  @CheckResult
  private inline fun createStatusListener(
      crossinline onDownloadComplete: () -> Unit
  ): InstallStateUpdatedListener {
    return InstallStateUpdatedListener { state ->
      Logger.d("Install state changed: ${state.installStatus()}")
      if (state.installStatus() == InstallStatus.DOWNLOADED) {
        Logger.d("Download completed!")
        onDownloadComplete()
      }
    }
  }

  override suspend fun complete() =
      withContext(context = Dispatchers.Main) {
        Enforcer.assertOnMainThread()

        return@withContext suspendCancellableCoroutine { continuation ->
          Logger.d("Now completing update...")
          manager
              .completeUpdate()
              .addOnFailureListener { err ->
                Logger.e(err, "UPDATE ERROR")
                continuation.resumeWithException(err)
              }
              .addOnSuccessListener {
                Logger.d("UPDATE COMPLETE")
                continuation.resume(Unit)
              }
        }
      }

  override suspend fun watchForDownloadComplete(onDownloadComplete: () -> Unit) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext suspendCancellableCoroutine<Unit> { continuation ->
          val listener = createStatusListener(onDownloadComplete)

          Logger.d("Listen for install status DOWNLOADED")
          manager.registerListener(listener)

          continuation.invokeOnCancellation {
            Logger.d("Stop listening for install status")
            manager.unregisterListener(listener)
          }
        }
      }

  override suspend fun checkForUpdate(): AppUpdateLauncher =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        if (isFake) {
          Logger.d("In debug mode we fake a delay to mimic real world network turnaround time.")
          delay(2000L)
        }

        return@withContext suspendCancellableCoroutine { continuation ->
          manager.appUpdateInfo
              .addOnFailureListener { error ->
                Logger.e(error, "Failed to resolve app update info task")
                continuation.resume(AppUpdateLauncher.empty())
              }
              .addOnSuccessListener { info ->
                Logger.d("App Update info received: $info")
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                  val updateType = AppUpdateType.FLEXIBLE
                  if (info.isUpdateTypeAllowed(updateType)) {
                    Logger.d("Update is available and flexible")
                    continuation.resume(PlayStoreAppUpdateLauncher(manager, info, updateType))
                    return@addOnSuccessListener
                  }
                }

                Logger.d("Update is not available")
                continuation.resume(AppUpdateLauncher.empty())
              }
        }
      }
}
