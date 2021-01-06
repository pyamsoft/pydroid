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

import android.app.Activity
import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.cachify.Cached
import com.pyamsoft.cachify.MemoryCacheStorage
import com.pyamsoft.cachify.cachify
import com.pyamsoft.pydroid.bootstrap.version.store.PlayStoreAppUpdater
import java.util.concurrent.TimeUnit.MINUTES

/**
 * In-App update module
 */
public class VersionModule(params: Parameters) {

    private val impl: VersionInteractorImpl

    init {
        val updater = PlayStoreAppUpdater(
            params.isFakeUpgradeChecker,
            params.context.applicationContext,
            params.version,
            params.isFakeUpgradeAvailable
        )
        val network = VersionInteractorNetwork(updater)
        impl = VersionInteractorImpl(updater, createCache(network))
    }

    /**
     * Provide version interactor
     */
    @CheckResult
    public fun provideInteractor(): VersionInteractor {
        return impl
    }

    public companion object {

        @JvmStatic
        @CheckResult
        private fun createCache(network: VersionInteractor): Cached<AppUpdateLauncher> {
            return cachify<AppUpdateLauncher>(
                storage = MemoryCacheStorage.create(30, MINUTES)
            ) { network.checkVersion(true) }
        }
    }

    /**
     * Module parameters
     */
    public data class Parameters(
        internal val context: Context,
        internal val version: Int,
        internal val isFakeUpgradeChecker: Boolean,
        internal val isFakeUpgradeAvailable: Boolean,
    )
}
