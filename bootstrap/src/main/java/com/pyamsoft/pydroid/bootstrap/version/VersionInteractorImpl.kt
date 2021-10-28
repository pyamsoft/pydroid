/*
 * Copyright 2020 Peter Kenji Yamanaka
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

import com.pyamsoft.cachify.Cache
import com.pyamsoft.cachify.Cached
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.Logger

import com.pyamsoft.pydroid.core.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class VersionInteractorImpl
internal constructor(
    private val updater: AppUpdater,
    private val updateCache: Cached<ResultWrapper<AppUpdateLauncher>>
) : VersionInteractor, Cache {

  override suspend fun watchForDownloadComplete(onDownloadCompleted: () -> Unit) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        return@withContext updater.watchForDownloadComplete(onDownloadCompleted)
      }

  override suspend fun completeUpdate() =
      withContext(context = Dispatchers.Main) {
        Enforcer.assertOnMainThread()

        Logger.d("GOING DOWN FOR UPDATE")
        updater.complete()
      }

  override suspend fun checkVersion(force: Boolean): ResultWrapper<AppUpdateLauncher> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        if (force) {
          updateCache.clear()
        }

        return@withContext updateCache.call()
      }

  override suspend fun clear() {
    Enforcer.assertOffMainThread()
    updateCache.clear()
  }
}
