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

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.cachify.Cached
import com.pyamsoft.cachify.MemoryCacheStorage
import com.pyamsoft.cachify.cachify
import com.pyamsoft.pydroid.bootstrap.version.store.PlayStoreAppUpdater
import java.util.concurrent.TimeUnit.MINUTES

class VersionCheckModule(params: Parameters) {

    private val impl: VersionInteractorImpl

    init {
        val updater = PlayStoreAppUpdater(params.context, params.debug, params.version)
        val network = VersionInteractorNetwork(updater)
        impl = VersionInteractorImpl(updater, createCache(network))
    }

    @CheckResult
    fun provideInteractor(): VersionInteractor {
        return impl
    }

    companion object {

        @JvmStatic
        @CheckResult
        private fun createCache(network: VersionInteractor): Cached<AppUpdateLauncher> {
            return cachify<AppUpdateLauncher>(
                storage = MemoryCacheStorage.create(30, MINUTES)
            ) { requireNotNull(network.checkVersion(true)) }
        }
    }

    data class Parameters(
        internal val context: Context,
        internal val version: Int,
        internal val debug: Boolean
    )
}
