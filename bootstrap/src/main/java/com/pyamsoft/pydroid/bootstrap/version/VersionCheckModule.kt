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
import com.popinnow.android.repo.moshi.MoshiPersister
import com.popinnow.android.repo.newRepoBuilder
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
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
  currentVersion: Int,
  debug: Boolean,
  private val schedulerProvider: SchedulerProvider
) {

  private val packageName: String = context.packageName
  private val cachedInteractor: VersionCheckInteractor

  private val moshi = Moshi.Builder()
      .build()

  private val repo = newRepoBuilder<Int>()
      .memoryCache(30, MINUTES)
      .persister(
          2, HOURS,
          File(context.cacheDir, "versioncache"),
          MoshiPersister.create(moshi, Int::class.javaObjectType)
      )
      .build()

  init {
    val versionCheckApi = VersionCheckApi(provideRetrofit(provideOkHttpClient(debug)))
    val versionCheckService = versionCheckApi.create(VersionCheckService::class.java)
    val networkStatusProvider = NetworkStatusProviderImpl(context.applicationContext)
    val minimumApiProvider = MinimumApiProviderImpl()

    val network =
      VersionCheckInteractorNetwork(
          enforcer, minimumApiProvider,
          networkStatusProvider, versionCheckService
      )
    cachedInteractor = VersionCheckInteractorImpl(enforcer, currentVersion, debug, network, repo)
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

  @CheckResult
  fun getMoshi(): Moshi {
    return moshi
  }

  @CheckResult
  fun getViewModel(): VersionCheckViewModel {
    return VersionCheckViewModel(
        packageName,
        cachedInteractor,
        schedulerProvider.foregroundScheduler,
        schedulerProvider.backgroundScheduler
    )
  }

  companion object {

    private const val GITHUB_URL = "raw.githubusercontent.com"
    private const val CURRENT_VERSION_REPO_BASE_URL =
      "https://$GITHUB_URL/pyamsoft/android-project-versions/master/"
  }
}
