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

package com.pyamsoft.pydroid.bootstrap.otherapps

import android.content.Context
import com.pyamsoft.pydroid.bootstrap.app.AppInteractorImpl
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherAppsService
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.ResultWrapper
import com.pyamsoft.pydroid.util.ifNotCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class OtherAppsInteractorNetwork
internal constructor(context: Context, private val service: OtherAppsService) :
    AppInteractorImpl(context), OtherAppsInteractor {

  override suspend fun getApps(force: Boolean): ResultWrapper<List<OtherApp>> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        return@withContext try {
          val result = service.getApps().apps()
          ResultWrapper.success(
              result.map { entry ->
                OtherApp(
                    entry.packageName(),
                    entry.name(),
                    entry.description(),
                    entry.icon(),
                    entry.url(),
                    entry.source())
              })
        } catch (throwable: Throwable) {
          throwable.ifNotCancellation {
            Logger.e(throwable, "Unable to fetch other apps payload")
            ResultWrapper.failure(throwable)
          }
        }
      }
}
