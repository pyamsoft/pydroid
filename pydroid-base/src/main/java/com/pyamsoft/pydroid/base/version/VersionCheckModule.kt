/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.base.version

import android.content.Context
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.base.version.api.MinimumApiProviderImpl
import com.pyamsoft.pydroid.base.version.network.NetworkStatusProviderImpl
import com.pyamsoft.pydroid.cache.repository
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class VersionCheckModule(
  context: Context,
  debug: Boolean
) {

  private val packageName: String = context.packageName
  private val cachedInteractor: VersionCheckInteractor

  init {
    val versionCheckApi = VersionCheckApi(
        provideRetrofit(provideOkHttpClient(debug), provideConverter())
    )

    val versionCheckService = versionCheckApi.create(VersionCheckService::class.java)
    val networkStatusProvider = NetworkStatusProviderImpl(context.applicationContext)
    val minimumApiProvider = MinimumApiProviderImpl()

    val network =
      VersionCheckInteractorNetwork(minimumApiProvider, networkStatusProvider, versionCheckService)
    val versionCache = repository<Int>()
    cachedInteractor = VersionCheckInteractorImpl(network, versionCache)
  }

  @CheckResult
  private fun provideConverter(): Converter.Factory {
    return MoshiConverterFactory.create(
        Moshi.Builder().add(AutoValueTypeAdapterFactory.create()).build()
    )
  }

  @CheckResult
  private fun provideOkHttpClient(debug: Boolean): OkHttpClient {
    return OkHttpClient.Builder()
        .apply {
          if (debug) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(logging)
          }
        }
        .build()
  }

  @CheckResult
  private fun provideRetrofit(
    okHttpClient: OkHttpClient,
    converter: Converter.Factory
  ): Retrofit {
    return Retrofit.Builder()
        .apply {
          baseUrl(CURRENT_VERSION_REPO_BASE_URL)
          client(okHttpClient)
          addConverterFactory(converter)
          addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }
        .build()
  }

  @CheckResult
  fun getPresenter(currentVersion: Int): VersionCheckPresenter {
    return VersionCheckPresenter(packageName, currentVersion, cachedInteractor)
  }

  companion object {

    private const val GITHUB_URL = "raw.githubusercontent.com"
    private const val CURRENT_VERSION_REPO_BASE_URL =
      "https://$GITHUB_URL/pyamsoft/android-project-versions/master/"
  }
}
