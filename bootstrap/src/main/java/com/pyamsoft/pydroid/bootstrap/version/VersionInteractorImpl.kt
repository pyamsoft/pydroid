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

package com.pyamsoft.pydroid.bootstrap.version

import com.pyamsoft.cachify.Cached
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class VersionInteractorImpl
internal constructor(
    private val networkInteractor: VersionInteractorNetwork,
    private val updateCache: Cached<ResultWrapper<AppUpdateLauncher>>
) : VersionInteractor, VersionInteractor.Cache {

  override suspend fun watchDownloadStatus(
      onDownloadProgress: (Float) -> Unit,
      onDownloadCompleted: () -> Unit
  ) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        return@withContext networkInteractor.watchDownloadStatus(
            onDownloadProgress = onDownloadProgress,
            onDownloadCompleted = onDownloadCompleted,
        )
      }

  override suspend fun completeUpdate() =
      withContext(context = Dispatchers.Main) {
        Enforcer.assertOnMainThread()
        return@withContext networkInteractor.completeUpdate()
      }

  override suspend fun checkVersion(): ResultWrapper<AppUpdateLauncher> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        return@withContext updateCache.call()
      }

  override suspend fun invalidateVersion() =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        updateCache.clear()
      }
}
