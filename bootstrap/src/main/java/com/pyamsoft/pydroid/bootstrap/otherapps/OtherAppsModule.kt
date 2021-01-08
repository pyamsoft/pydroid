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
import androidx.annotation.CheckResult
import com.pyamsoft.cachify.Cached
import com.pyamsoft.cachify.MemoryCacheStorage
import com.pyamsoft.cachify.cachify
import com.pyamsoft.pydroid.bootstrap.network.ServiceCreator
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherAppsService
import java.util.concurrent.TimeUnit.HOURS

/**
 * Module for other pyamsoft applications
 */
public class OtherAppsModule(params: Parameters) {

    private val impl: OtherAppsInteractorImpl

    init {
        val otherAppsService = params.serviceCreator.createService(OtherAppsService::class.java)

        val network = OtherAppsInteractorNetwork(
            params.context.applicationContext,
            otherAppsService
        )

        impl = OtherAppsInteractorImpl(
            params.context.applicationContext,
            params.packageName,
            createCache(network)
        )
    }

    /**
     * Provide an interactor for other pyamsoft applications
     */
    @CheckResult
    public fun provideInteractor(): OtherAppsInteractor {
        return impl
    }

    public companion object {

        @JvmStatic
        @CheckResult
        private fun createCache(
            network: OtherAppsInteractor
        ): Cached<Result<List<OtherApp>>> {
            return cachify<Result<List<OtherApp>>>(
                storage = { listOf(MemoryCacheStorage.create(24, HOURS)) }
            ) { requireNotNull(network.getApps(true)) }
        }
    }

    /**
     * Module parameters
     */
    public data class Parameters(
        internal val context: Context,
        internal val packageName: String,
        internal val serviceCreator: ServiceCreator
    )
}
