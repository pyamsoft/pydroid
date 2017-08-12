/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.version

import android.support.annotation.CheckResult
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pyamsoft.pydroid.PYDroidModule
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class VersionCheckModule(pyDroidModule: PYDroidModule) {

  private val cachedInteractor: VersionCheckInteractor
  private val computationScheduler: Scheduler = pyDroidModule.provideComputationScheduler()
  private val ioScheduler: Scheduler = pyDroidModule.provideIoScheduler()
  private val mainThreadScheduler: Scheduler = pyDroidModule.provideMainThreadScheduler()

  init {
    val versionCheckApi: VersionCheckApi = VersionCheckApi(
        provideRetrofit(provideOkHttpClient(pyDroidModule.isDebug), provideGson()))
    val versionCheckService: VersionCheckService = versionCheckApi.create(
        VersionCheckService::class.java)
    val interactor: VersionCheckInteractor = VersionCheckInteractorImpl(versionCheckService)
    cachedInteractor = VersionCheckInteractorCache(interactor)
  }

  @CheckResult private fun provideGson(): Gson {
    val gsonBuilder = GsonBuilder().registerTypeAdapterFactory(AutoValueTypeAdapterFactory.create())
    return gsonBuilder.create()
  }

  @CheckResult private fun provideOkHttpClient(debug: Boolean): OkHttpClient {
    val builder = OkHttpClient.Builder()
    if (debug) {
      val logging = HttpLoggingInterceptor()
      logging.level = HttpLoggingInterceptor.Level.BODY
      builder.addInterceptor(logging)
    }

    val pinner = CertificatePinner.Builder().add(GITHUB_URL,
        "sha256/m41PSCmB5CaR0rKh7VMMXQbDFgCNFXchcoNFm3RuoXw=").add(GITHUB_URL,
        "sha256/k2v657xBsOVe1PQRwOsHsw3bsGT2VzIqz5K+59sNQws=").add(GITHUB_URL,
        "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=").build()
    builder.certificatePinner(pinner)

    return builder.build()
  }

  @CheckResult private fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder().baseUrl(CURRENT_VERSION_REPO_BASE_URL).client(
        okHttpClient).addConverterFactory(GsonConverterFactory.create(gson)).addCallAdapterFactory(
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread())).build()
  }

  @CheckResult fun getPresenter(packageName: String, currentVersion: Int): VersionCheckPresenter {
    return VersionCheckPresenter(packageName, currentVersion, cachedInteractor,
        computationScheduler, ioScheduler, mainThreadScheduler)
  }

  companion object {

    private const val GITHUB_URL = "raw.githubusercontent.com"
    private const val CURRENT_VERSION_REPO_BASE_URL = "https://$GITHUB_URL/pyamsoft/android-project-versions/master/"
  }
}
