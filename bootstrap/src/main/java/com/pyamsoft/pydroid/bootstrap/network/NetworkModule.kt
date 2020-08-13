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

package com.pyamsoft.pydroid.bootstrap.network

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.Enforcer
import com.squareup.moshi.Moshi
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.net.SocketFactory

class NetworkModule(params: Parameters) {

    private val serviceCreator: ServiceCreator
    private val callFactory: Call.Factory
    private val converterFactory: Converter.Factory

    init {
        val debug = params.debug
        callFactory = OkHttpClientLazyCallFactory(debug)
        converterFactory = MoshiConverterFactory.create(createMoshi())
        val retrofit = createRetrofit(callFactory, converterFactory)

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

    @CheckResult
    fun provideCallFactory(): Call.Factory {
        return callFactory
    }

    @CheckResult
    fun provideConverterFactory(): Converter.Factory {
        return converterFactory
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
        private fun createRetrofit(
            callFactory: Call.Factory,
            converterFactory: Converter.Factory
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl(CURRENT_VERSION_REPO_BASE_URL)
                .callFactory(callFactory)
                .addConverterFactory(converterFactory)
                .build()
        }

        /**
         * Creates the OkHttpClient lazily to avoid small main thread work
         */
        private class OkHttpClientLazyCallFactory(debug: Boolean) : Call.Factory {

            private val client by lazy {
                createOkHttpClient(debug, DelegatingSocketFactory.create())
            }

            override fun newCall(request: Request): Call {
                Enforcer.assertOffMainThread()
                return client.newCall(request)
            }

            companion object {

                @JvmStatic
                @CheckResult
                private fun createOkHttpClient(
                    debug: Boolean,
                    socketFactory: SocketFactory
                ): OkHttpClient {
                    Enforcer.assertOffMainThread()

                    return OkHttpClient.Builder()
                        .socketFactory(socketFactory)
                        .also {
                            if (debug) {
                                val logging = HttpLoggingInterceptor()
                                logging.level = HttpLoggingInterceptor.Level.BODY
                                it.addInterceptor(logging)
                            }
                        }
                        .build()
                }
            }
        }
    }

    data class Parameters(
        internal val debug: Boolean
    )
}
