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

package com.pyamsoft.pydroid.bootstrap.version

import android.content.Context
import androidx.annotation.CheckResult
import com.popinnow.android.repo.newRepoBuilder
import com.pyamsoft.pydroid.bootstrap.version.api.MinimumApiProviderImpl
import com.pyamsoft.pydroid.bootstrap.version.network.DelegatingSocketFactory
import com.pyamsoft.pydroid.bootstrap.version.network.NetworkStatusProviderImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.net.SocketFactory

class VersionCheckModule(
  context: Context,
  debug: Boolean
) {

  private val packageName: String = context.packageName
  private val cachedInteractor: VersionCheckInteractor

  init {
    val versionCheckApi = VersionCheckApi(provideRetrofit(provideOkHttpClient(debug)))
    val versionCheckService = versionCheckApi.create(VersionCheckService::class.java)
    val networkStatusProvider = NetworkStatusProviderImpl(context.applicationContext)
    val minimumApiProvider = MinimumApiProviderImpl()

    val network =
      VersionCheckInteractorNetwork(minimumApiProvider, networkStatusProvider, versionCheckService)
    val versionCache = newRepoBuilder<Int>().memoryCache()
        .buildSingle()
    cachedInteractor = VersionCheckInteractorImpl(network, versionCache)
  }

  @CheckResult
  private fun provideOkHttpClient(debug: Boolean): OkHttpClient {
    return OkHttpClient.Builder()
        .socketFactory(DelegatingSocketFactory(SocketFactory.getDefault()))
        .also {
          if (debug) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            it.addInterceptor(logging)
          }
        }
        .build()
  }

  @CheckResult
  private fun provideRetrofit(
    okHttpClient: OkHttpClient
  ): Retrofit {
    return Retrofit.Builder()
        .baseUrl(CURRENT_VERSION_REPO_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
