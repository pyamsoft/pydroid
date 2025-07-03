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

package com.pyamsoft.pydroid.bootstrap.version.play

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.ResultWrapper
import com.pyamsoft.pydroid.util.ifNotCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal class PlayStoreAppUpdateLauncher
internal constructor(
    private val manager: AppUpdateManager,
    private val info: AppUpdateInfo,
    @param:AppUpdateType private val type: Int,
) : AppUpdateLauncher {

  private suspend fun FakeAppUpdateManager.fakeUpdate() {
    val self = this

    Logger.d { "User accepts fake update" }
    self.userAcceptsUpdate()

    Logger.d { "Start a fake download" }

    // Mark download started (we need this first to then set bytes)
    self.downloadStarts()

    // Set total bytes
    val totalBytes = 100L
    self.setTotalBytesToDownload(totalBytes)

    // Download the update
    var downloaded = 0L
    while (downloaded < totalBytes) {
      delay(100L)
      downloaded += 1
      self.setBytesDownloaded(downloaded)
    }

    Logger.d { "Complete a fake download" }
    self.downloadCompletes()
  }

  override fun availableUpdateVersion(): Int {
    return if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
      info.availableVersionCode()
    } else {
      0
    }
  }

  override suspend fun update(activity: Activity, requestCode: Int): ResultWrapper<Unit> =
      withContext(context = Dispatchers.Main) {
        return@withContext try {
          Logger.d { "Begin update flow $requestCode $info" }

          val options = AppUpdateOptions.defaultOptions(type)
          if (manager.startUpdateFlowForResult(info, activity, options, requestCode)) {
            Logger.d { "Update flow has started" }
            if (manager is FakeAppUpdateManager) {
              manager.fakeUpdate()
            }
          }

          ResultWrapper.success(Unit)
        } catch (e: Throwable) {
          e.ifNotCancellation {
            Logger.e(e) { "Failed to launch In-App update flow" }
            ResultWrapper.failure(e)
          }
        }
      }
}
