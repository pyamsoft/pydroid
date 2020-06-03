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

package com.pyamsoft.pydroid.bootstrap.network

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.Enforcer
import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkModule(params: Parameters) {

    private val serviceCreator: ServiceCreator

    init {
        val debug = params.debug
        val moshi = createMoshi()
        val retrofit = createRetrofit(moshi) { createOkHttpClient(debug) }
        serviceCreator = object : ServiceCreator {
            override fun <S : Any> createService(serviceClass: Class<S>): S {
                return retrofit.create(serviceClass)
            }
        }
    }

    @CheckResult
    fun provideServiceCreator(): ServiceCreator {
        return serviceCreator
    }

    companion object {

        private const val CURRENT_VERSION_REPO_BASE_URL =
            "https://raw.githubusercontent.com/pyamsoft/android-project-versions/master/"

        @JvmStatic
        @CheckResult
        private fun createMoshi(): Moshi {
            return Moshi.Builder()
                .build()
        }

        @JvmStatic
        @CheckResult
        private fun createOkHttpClient(debug: Boolean): OkHttpClient {
            Enforcer.assertNotOnMainThread()

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
            moshi: Moshi,
            clientProvider: () -> OkHttpClient
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(CURRENT_VERSION_REPO_BASE_URL)
                .callFactory(OkHttpClientLazyCallFactory(clientProvider))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        private class OkHttpClientLazyCallFactory(
            provider: () -> OkHttpClient
        ) : Call.Factory {

            private val client by lazy { provider() }

            override fun newCall(request: Request): Call {
                Enforcer.assertNotOnMainThread()
                return client.newCall(request)
            }
        }
    }

    data class Parameters(
        internal val debug: Boolean
    )
}
