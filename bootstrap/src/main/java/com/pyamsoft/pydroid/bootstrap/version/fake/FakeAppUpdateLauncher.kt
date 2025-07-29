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

package com.pyamsoft.pydroid.bootstrap.version.fake

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.pyamsoft.pydroid.bootstrap.version.AbstractAppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateResultStatus
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class FakeAppUpdateLauncher
internal constructor(
    private val manager: FakeAppUpdateManager,
    info: AppUpdateInfo,
    @AppUpdateType type: Int,
    private val fakeUpgradeRequest: FakeUpgradeRequest,
) : AbstractAppUpdateLauncher(manager, info, type) {

  private suspend fun FakeAppUpdateManager.fakeDownload(
      totalBytes: Long,
      downloadCompletedAmount: Long
  ) {
    val self = this

    // Mark download started (we need this first to then set bytes)
    self.downloadStarts()
    self.setTotalBytesToDownload(totalBytes)

    // Initialize the update to 0
    self.setBytesDownloaded(0)

    var downloaded = 0L
    while (downloaded < downloadCompletedAmount) {
      delay(100L)
      downloaded += 1
      self.setBytesDownloaded(downloaded)
    }
  }

  private suspend fun FakeAppUpdateManager.fakeUpdate() {
    val self = this

    when (fakeUpgradeRequest) {
      FakeUpgradeRequest.USER_ACCEPTED_DOWNLOAD_SUCCESS_INSTALL_SUCCESS,
      FakeUpgradeRequest.USER_ACCEPTED_DOWNLOAD_SUCCESS_INSTALL_FAILURE -> {
        Logger.d { "User accepts fake update" }
        self.userAcceptsUpdate()

        Logger.d { "Start a fake download" }
        val totalBytes = 100L

        // Fake a complete download
        self.fakeDownload(totalBytes, totalBytes)

        Logger.d { "Complete a fake download" }
        self.downloadCompletes()
      }

      FakeUpgradeRequest.USER_ACCEPTED_DOWNLOAD_CANCELLED -> {
        Logger.d { "User accepts fake update" }
        self.userAcceptsUpdate()

        Logger.d { "Start a fake download" }
        val totalBytes = 100L
        // Fake a halfway complete download and then cancel in the middle
        self.fakeDownload(totalBytes, totalBytes / 2)

        Logger.d { "User cancels fake download" }
        self.userCancelsDownload()
      }

      FakeUpgradeRequest.USER_ACCEPTED_DOWNLOAD_FAILS -> {
        Logger.d { "User accepts fake update" }
        self.userAcceptsUpdate()

        Logger.d { "Start a fake download" }

        val totalBytes = 100L
        // Fake a halfway complete download and then cancel in the middle
        self.fakeDownload(totalBytes, totalBytes / 2)

        Logger.d { "Fake download fails in the middle" }
        self.downloadFails()
      }

      FakeUpgradeRequest.USER_REJECTED_DOWNLOAD -> {
        Logger.w { "In a fake rejected flow, this ACCEPT handler should not be happening" }
      }
    }
  }

  override fun onBeforeUpdateFlowStarted(): ResultWrapper<AppUpdateResultStatus>? {
    if (fakeUpgradeRequest == FakeUpgradeRequest.USER_REJECTED_DOWNLOAD) {
      Logger.d { "User rejects fake update" }
      manager.userRejectsUpdate()

      // According to docs, we have to force simulate the RESULT_CANCELED
      // https://developer.android.com/reference/com/google/android/play/core/appupdate/testing/FakeAppUpdateManager.html#userRejectsUpdate()
      return ResultWrapper.success(AppUpdateResultStatus.USER_CANCELLED)
    }

    return null
  }

  override fun onAfterUpdateFlowStarted(
      activity: ComponentActivity,
      status: AppUpdateResultStatus
  ) {
    // In the background, we "download"
    // Use the activity scope to fake this so that this background activity
    // does not prevent the actual flow from returning
    activity.lifecycleScope.launch(context = Dispatchers.IO) {
      Logger.d { "User has accepted the fake in-app update prompt." }
      manager.fakeUpdate()
    }
  }
}
