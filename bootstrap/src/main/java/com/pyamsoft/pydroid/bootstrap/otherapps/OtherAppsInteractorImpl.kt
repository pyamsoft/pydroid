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

package com.pyamsoft.pydroid.bootstrap.otherapps

import android.content.Context
import com.pyamsoft.cachify.Cache
import com.pyamsoft.cachify.Cached
import com.pyamsoft.pydroid.bootstrap.app.AppInteractorImpl
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class OtherAppsInteractorImpl
internal constructor(
    context: Context,
    private val packageName: String,
    private val otherAppsCache: Cached<ResultWrapper<List<OtherApp>>>
) : AppInteractorImpl(context), OtherAppsInteractor, Cache {

  override suspend fun getApps(force: Boolean): ResultWrapper<List<OtherApp>> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        if (force) {
          otherAppsCache.clear()
        }

        // Ignore the app we have open right now in the list
        return@withContext otherAppsCache.call().map { apps ->
          apps.asSequence()
              .filterNot { it.packageName == packageName }
              .sortedBy { it.name }
              .toList()
        }
      }

  override suspend fun clear() {
    Enforcer.assertOffMainThread()
    otherAppsCache.clear()
  }
}
