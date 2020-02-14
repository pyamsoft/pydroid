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
import com.pyamsoft.pydroid.bootstrap.network.DelegatingSocketFactory
import com.pyamsoft.pydroid.bootstrap.version.api.MinimumApiProviderImpl
import com.pyamsoft.pydroid.bootstrap.version.api.UpdatePayload
import com.pyamsoft.pydroid.bootstrap.version.api.VersionCheckService
import com.pyamsoft.pydroid.core.Enforcer
import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit.MINUTES

class VersionCheckModule(params: Parameters) {

    private val impl: VersionCheckInteractorImpl
    private val moshi: Moshi

    init {
        val debug = params.debug
        val enforcer = params.enforcer
        val currentVersion = params.currentVersion
        val packageName = params.packageName

        moshi = createMoshi()
        val retrofit = createRetrofit(enforcer, moshi) { createOkHttpClient(enforcer, debug) }
        val versionCheckService = createService(retrofit)
        val minimumApiProvider = MinimumApiProviderImpl()

        val network = VersionCheckInteractorNetwork(
            currentVersion,
            packageName,
            enforcer,
            minimumApiProvider,
            versionCheckService
        )

        impl = VersionCheckInteractorImpl(debug, enforcer, createCache(debug, network))
    }

    @CheckResult
    fun provideInteractor(): VersionCheckInteractor {
        return impl
    }

    companion object {

        private const val GITHUB_URL = "raw.githubusercontent.com"
        private const val CURRENT_VERSION_REPO_BASE_URL =
            "https://$GITHUB_URL/pyamsoft/android-project-versions/master/"

        @JvmStatic
        @CheckResult
        private fun createService(retrofit: Retrofit): VersionCheckService {
            return retrofit.create(VersionCheckService::class.java)
        }

        @JvmStatic
        @CheckResult
        private fun createMoshi(): Moshi {
            return Moshi.Builder()
                .build()
        }

        @JvmStatic
        @CheckResult
        private fun createCache(
            debug: Boolean,
            network: VersionCheckInteractor
        ): Cached<UpdatePayload> {
            return cachify<UpdatePayload>(
                storage = MemoryCacheStorage.create(30, MINUTES),
                debug = debug
            ) { requireNotNull(network.checkVersion(true)) }
        }

        @JvmStatic
        @CheckResult
        private fun createOkHttpClient(enforcer: Enforcer, debug: Boolean): OkHttpClient {
            enforcer.assertNotOnMainThread()

            return OkHttpClient.Builder()
                .socketFactory(DelegatingSocketFactory.create())
                .also {
                    if (debug) {
                        val logging = HttpLoggingInterceptor()
                        logging.level = HttpLoggingInterceptor.Level.BODY
                        it.addInterceptor(logging)
                    }
                }
                .build()
        }

        @JvmStatic
        @CheckResult
        private fun createRetrofit(
            enforcer: Enforcer,
            moshi: Moshi,
            clientProvider: () -> OkHttpClient
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(CURRENT_VERSION_REPO_BASE_URL)
                .callFactory(OkHttpClientLazyCallFactory(enforcer, clientProvider))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        private class OkHttpClientLazyCallFactory(
            private val enforcer: Enforcer,
            provider: () -> OkHttpClient
        ) : Call.Factory {

            private val client by lazy { provider() }

            override fun newCall(request: Request): Call {
                enforcer.assertNotOnMainThread()
                return client.newCall(request)
            }
        }
    }

    data class Parameters(
        internal val debug: Boolean,
        internal val currentVersion: Int,
        internal val packageName: String,
        internal val enforcer: Enforcer
    )
}
