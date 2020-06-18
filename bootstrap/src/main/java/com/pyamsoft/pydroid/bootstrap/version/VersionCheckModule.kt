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
import com.pyamsoft.cachify.Cached
import com.pyamsoft.cachify.MemoryCacheStorage
import com.pyamsoft.cachify.cachify
import com.pyamsoft.pydroid.bootstrap.network.ServiceCreator
import com.pyamsoft.pydroid.bootstrap.version.api.MinimumApiProviderImpl
import com.pyamsoft.pydroid.bootstrap.version.api.UpdatePayload
import com.pyamsoft.pydroid.bootstrap.version.api.VersionCheckService
import java.util.concurrent.TimeUnit.MINUTES

class VersionCheckModule(params: Parameters) {

    private val impl: VersionCheckInteractorImpl

    init {
        val debug = params.debug
        val currentVersion = params.currentVersion
        val packageName = params.packageName

        val versionCheckService =
            params.serviceCreator.createService(VersionCheckService::class.java)
        val minimumApiProvider = MinimumApiProviderImpl()

        val network = VersionCheckInteractorNetwork(
            currentVersion,
            packageName,
            minimumApiProvider,
            versionCheckService
        )

        impl = VersionCheckInteractorImpl(debug, createCache(network))
    }

    @CheckResult
    fun provideInteractor(): VersionCheckInteractor {
        return impl
    }

    companion object {

        @JvmStatic
        @CheckResult
        private fun createCache(
            network: VersionCheckInteractor
        ): Cached<UpdatePayload> {
            return cachify<UpdatePayload>(
                storage = MemoryCacheStorage.create(30, MINUTES)
            ) { requireNotNull(network.checkVersion(true)) }
        }
    }

    data class Parameters(
        internal val debug: Boolean,
        internal val currentVersion: Int,
        internal val packageName: String,
        internal val serviceCreator: ServiceCreator
    )
}
