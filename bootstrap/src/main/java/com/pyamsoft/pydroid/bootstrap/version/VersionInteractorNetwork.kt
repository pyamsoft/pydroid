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

package com.pyamsoft.pydroid.bootstrap.version

import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdater
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.util.ifNotCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class VersionInteractorNetwork
internal constructor(
    private val updater: AppUpdater,
) : VersionInteractor {

  override suspend fun watchDownloadStatus(
      onDownloadProgress: (Float) -> Unit,
      onDownloadCompleted: () -> Unit
  ) =
      withContext(context = Dispatchers.Default) {
        updater.watchDownloadStatus(
            onDownloadProgress = onDownloadProgress,
            onDownloadCompleted = onDownloadCompleted,
        )
      }

  override suspend fun completeUpdate() =
      withContext(context = Dispatchers.Default) {
        Logger.d { "GOING DOWN FOR UPDATE" }
        updater.complete()
      }

  override suspend fun checkVersion(): ResultWrapper<AppUpdateLauncher> =
      withContext(context = Dispatchers.Default) {
        return@withContext try {
          ResultWrapper.success(updater.checkForUpdate())
        } catch (e: Throwable) {
          e.ifNotCancellation {
            Logger.e(e) { "Failed to check for updates" }
            ResultWrapper.failure(e)
          }
        }
      }
}
