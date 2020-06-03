/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.bootstrap.version

import com.pyamsoft.cachify.Cache
import com.pyamsoft.cachify.Cached
import com.pyamsoft.pydroid.bootstrap.version.api.UpdatePayload
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class VersionCheckInteractorImpl internal constructor(
    private val debug: Boolean,
    private val updateCache: Cached<UpdatePayload>
) : VersionCheckInteractor, Cache<Any> {

    override suspend fun checkVersion(force: Boolean): UpdatePayload? =
        withContext(context = Dispatchers.IO) {
            Enforcer.assertNotOnMainThread()

            if (force) {
                updateCache.clear()
            }

            val result = requireNotNull(updateCache.call())
            if (result.currentVersion < result.newVersion || (debug && force)) {
                return@withContext result
            } else {
                return@withContext null
            }
        }

    override suspend fun clear() {
        Enforcer.assertNotOnMainThread()
        updateCache.clear()
    }
}
