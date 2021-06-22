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

package com.pyamsoft.pydroid.bootstrap.version.store

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class PlayStoreAppUpdateLauncher
internal constructor(
    private val manager: AppUpdateManager,
    private val info: AppUpdateInfo,
    @AppUpdateType private val type: Int,
) : AppUpdateLauncher {

  override suspend fun update(activity: Activity, requestCode: Int): ResultWrapper<Unit> =
      withContext(context = Dispatchers.Main) {
        Enforcer.assertOnMainThread()

        return@withContext try {
          Timber.d("Begin update flow $requestCode $info")
          if (manager.startUpdateFlowForResult(info, type, activity, requestCode)) {
            Timber.d("Update flow has started")
            if (manager is FakeAppUpdateManager) {
              Timber.d("User accepts fake update")
              manager.userAcceptsUpdate()

              Timber.d("Start a fake download")
              manager.downloadStarts()

              Timber.d("Complete a fake download")
              manager.downloadCompletes()
            }
          }

          ResultWrapper.success(Unit)
        } catch (e: Throwable) {
          Timber.e(e, "Failed to launch In-App update flow")
          ResultWrapper.failure(e)
        }
      }
}
