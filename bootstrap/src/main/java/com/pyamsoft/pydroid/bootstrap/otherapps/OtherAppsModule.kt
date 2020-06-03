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

package com.pyamsoft.pydroid.bootstrap.otherapps

import androidx.annotation.CheckResult
import com.pyamsoft.cachify.Cached
import com.pyamsoft.cachify.MemoryCacheStorage
import com.pyamsoft.cachify.cachify
import com.pyamsoft.pydroid.bootstrap.network.ServiceCreator
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherAppsService
import java.util.concurrent.TimeUnit.HOURS

class OtherAppsModule(params: Parameters) {

    private val impl: OtherAppsInteractorImpl

    init {
        val debug = params.debug
        val otherAppsService = params.serviceCreator.createService(OtherAppsService::class.java)
        val network = OtherAppsInteractorNetwork(otherAppsService)
        impl = OtherAppsInteractorImpl(params.packageName, createCache(debug, network))
    }

    @CheckResult
    fun provideInteractor(): OtherAppsInteractor {
        return impl
    }

    companion object {

        @JvmStatic
        @CheckResult
        private fun createCache(
            debug: Boolean,
            network: OtherAppsInteractor
        ): Cached<List<OtherApp>> {
            return cachify<List<OtherApp>>(
                storage = MemoryCacheStorage.create(24, HOURS),
                debug = debug
            ) { requireNotNull(network.getApps(true)) }
        }
    }

    data class Parameters(
        internal val debug: Boolean,
        internal val packageName: String,
        internal val serviceCreator: ServiceCreator
    )
}
