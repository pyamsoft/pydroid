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

import android.content.Context
import androidx.annotation.CheckResult
import com.popinnow.android.repo.moshi.MoshiPersister
import com.popinnow.android.repo.newRepoBuilder
import com.pyamsoft.pydroid.bootstrap.version.api.MinimumApiProviderImpl
import com.pyamsoft.pydroid.bootstrap.version.network.NetworkStatusProviderImpl
import com.pyamsoft.pydroid.bootstrap.version.socket.DelegatingSocketFactory
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MINUTES

class VersionCheckModule(
  context: Context,
  enforcer: Enforcer,
  debug: Boolean,
  val currentVersion: Int
) {

  val interactor: VersionCheckInteractor

  val moshi: Moshi = Moshi.Builder()
      .build()

  private val repo = newRepoBuilder<UpdatePayload>()
      .memoryCache(30, MINUTES)
      .persister(
          2, HOURS,
          File(context.cacheDir, "versioncache"),
          MoshiPersister.create(moshi, UpdatePayload::class.java)
      )
      .build()

  init {
    val versionCheckApi = VersionCheckApi(provideRetrofit(provideOkHttpClient(debug)))
    val versionCheckService = versionCheckApi.create(VersionCheckService::class.java)
    val networkStatusProvider = NetworkStatusProviderImpl(context)
    val minimumApiProvider = MinimumApiProviderImpl()

    val network = VersionCheckInteractorNetwork(
        currentVersion,
        context.packageName,
        enforcer, minimumApiProvider,
        networkStatusProvider, versionCheckService
    )

    interactor = VersionCheckInteractorImpl(enforcer, debug, network, repo)
  }

  @CheckResult
  private fun provideOkHttpClient(debug: Boolean): OkHttpClient {
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

  @CheckResult
  private fun provideRetrofit(
    okHttpClient: OkHttpClient
  ): Retrofit {
    return Retrofit.Builder()
        .baseUrl(CURRENT_VERSION_REPO_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
  }

  companion object {

    private const val GITHUB_URL = "raw.githubusercontent.com"
    private const val CURRENT_VERSION_REPO_BASE_URL =
      "https://$GITHUB_URL/pyamsoft/android-project-versions/master/"
  }
}
