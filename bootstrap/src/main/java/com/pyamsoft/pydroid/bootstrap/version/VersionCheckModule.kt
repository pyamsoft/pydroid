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
import com.popinnow.android.repo.Repo
import com.popinnow.android.repo.moshi.MoshiPersister
import com.popinnow.android.repo.newRepoBuilder
import com.pyamsoft.pydroid.bootstrap.network.NetworkStatusProvider
import com.pyamsoft.pydroid.bootstrap.network.NetworkStatusProviderImpl
import com.pyamsoft.pydroid.bootstrap.network.socket.DelegatingSocketFactory
import com.pyamsoft.pydroid.bootstrap.version.api.MinimumApiProvider
import com.pyamsoft.pydroid.bootstrap.version.api.MinimumApiProviderImpl
import com.pyamsoft.pydroid.bootstrap.version.api.UpdatePayload
import com.pyamsoft.pydroid.bootstrap.version.api.VersionCheckService
import com.pyamsoft.pydroid.core.cache.Cache
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MINUTES
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class VersionCheckModule {

  @Binds
  @CheckResult
  @Named("version_check_network")
  internal abstract fun bindNetwork(impl: VersionCheckInteractorNetwork): VersionCheckInteractor

  @Binds
  @CheckResult
  @Named("cache_version")
  internal abstract fun bindCache(impl: VersionCheckInteractorImpl): Cache

  @Binds
  @CheckResult
  internal abstract fun bindInteractor(impl: VersionCheckInteractorImpl): VersionCheckInteractor

  @Binds
  @CheckResult
  internal abstract fun bindNetworkStatus(impl: NetworkStatusProviderImpl): NetworkStatusProvider

  @Binds
  @CheckResult
  internal abstract fun bindMinimumApi(impl: MinimumApiProviderImpl): MinimumApiProvider

  @Module
  companion object {

    private const val GITHUB_URL = "raw.githubusercontent.com"
    private const val CURRENT_VERSION_REPO_BASE_URL =
      "https://$GITHUB_URL/pyamsoft/android-project-versions/master/"

    @JvmStatic
    @CheckResult
    @Provides
    @Singleton
    internal fun provideService(retrofit: Retrofit): VersionCheckService {
      return retrofit.create(VersionCheckService::class.java)
    }

    @JvmStatic
    @CheckResult
    @Provides
    @Singleton
    internal fun provideMoshi(): Moshi {
      return Moshi.Builder()
          .build()
    }

    @JvmStatic
    @CheckResult
    @Provides
    @Singleton
    internal fun provideRepo(
      context: Context,
      moshi: Moshi
    ): Repo<UpdatePayload> {
      return newRepoBuilder<UpdatePayload>()
          .memoryCache(30, MINUTES)
          .persister(
              2, HOURS,
              File(context.cacheDir, "versioncache"),
              MoshiPersister.create(moshi, UpdatePayload::class.java)
          )
          .build()
    }

    @JvmStatic
    @CheckResult
    @Provides
    @Singleton
    internal fun provideOkHttpClient(@Named("debug") debug: Boolean): OkHttpClient {
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
    @Provides
    @Singleton
    internal fun provideRetrofit(
      okHttpClient: OkHttpClient,
      moshi: Moshi
    ): Retrofit {
      return Retrofit.Builder()
          .baseUrl(CURRENT_VERSION_REPO_BASE_URL)
          .client(okHttpClient)
          .addConverterFactory(MoshiConverterFactory.create(moshi))
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .build()
    }

  }
}
