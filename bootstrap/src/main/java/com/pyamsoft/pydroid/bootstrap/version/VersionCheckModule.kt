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
import com.pyamsoft.pydroid.bootstrap.network.DelegatingSocketFactory
import com.pyamsoft.pydroid.bootstrap.version.api.MinimumApiProviderImpl
import com.pyamsoft.pydroid.bootstrap.version.api.UpdatePayload
import com.pyamsoft.pydroid.bootstrap.version.api.VersionCheckService
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
  debug: Boolean,
  currentVersion: Int,
  packageName: String,
  enforcer: Enforcer
) {

  private val impl: VersionCheckInteractorImpl
  private val moshi: Moshi

  init {
    moshi = createMoshi()
    val okHttpClient = createOkHttpClient(debug)
    val retrofit = createRetrofit(okHttpClient, moshi)
    val repo = createRepo(context, moshi)
    val versionCheckService = createService(retrofit)
    val minimumApiProvider = MinimumApiProviderImpl()

    val network = VersionCheckInteractorNetwork(
        currentVersion,
        packageName,
        enforcer,
        minimumApiProvider,
        versionCheckService
    )

    impl = VersionCheckInteractorImpl(debug, network, enforcer, repo)
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
    private fun createRepo(
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
    private fun createOkHttpClient(debug: Boolean): OkHttpClient {
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
