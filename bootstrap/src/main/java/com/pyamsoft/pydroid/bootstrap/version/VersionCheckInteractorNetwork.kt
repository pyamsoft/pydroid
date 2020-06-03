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

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.version.api.MinimumApiProvider
import com.pyamsoft.pydroid.bootstrap.version.api.UpdatePayload
import com.pyamsoft.pydroid.bootstrap.version.api.VersionCheckResponse
import com.pyamsoft.pydroid.bootstrap.version.api.VersionCheckService
import com.pyamsoft.pydroid.core.Enforcer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class VersionCheckInteractorNetwork internal constructor(
    private val currentVersion: Int,
    private val packageName: String,
    private val minimumApiProvider: MinimumApiProvider,
    private val service: VersionCheckService
) : VersionCheckInteractor {

    @CheckResult
    private fun versionCodeForApi(
        response: VersionCheckResponse
    ): Int {
        Enforcer.assertOffMainThread()
        val minApi = minimumApiProvider.minApi()
        var versionCode = 0
        response.responseObjects()
            .asSequence()
            .sortedBy { it.minApi() }
            .forEach {
                if (it.minApi() <= minApi) {
                    versionCode = it.version()
                }
            }
        return versionCode
    }

    override suspend fun checkVersion(force: Boolean): UpdatePayload =
        withContext(context = Dispatchers.IO) {
            Enforcer.assertOffMainThread()
            val targetName = if (packageName.endsWith(".dev")) {
                packageName.substringBefore(".dev")
            } else {
                packageName
            }
            val result = service.checkVersion(targetName)
            return@withContext UpdatePayload(currentVersion, versionCodeForApi(result))
        }
}
