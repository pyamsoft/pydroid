/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.base.version

import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope.LIBRARY
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

@RestrictTo(LIBRARY)
class VersionCheckModule(pyDroidModule: PYDroidModule) {

    private val cachedInteractor: VersionCheckInteractor
    private val computationScheduler: Scheduler = pyDroidModule.provideComputationScheduler()
    private val ioScheduler: Scheduler = pyDroidModule.provideIoScheduler()
    private val mainThreadScheduler: Scheduler = pyDroidModule.provideMainThreadScheduler()

    init {
        val versionCheckApi = VersionCheckApi(
            provideRetrofit(provideOkHttpClient(pyDroidModule.isDebug), provideGson())
        )
        val versionCheckService: VersionCheckService = versionCheckApi.create(
            VersionCheckService::class.java
        )
        val interactor: VersionCheckInteractor = VersionCheckInteractorImpl(
            versionCheckService
        )
        cachedInteractor = VersionCheckInteractorCache(interactor)
    }

    @CheckResult
    private fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder().registerTypeAdapterFactory(
            AutoValueTypeAdapterFactory.create()
        )
        return gsonBuilder.create()
    }

    @CheckResult
    private fun provideOkHttpClient(debug: Boolean): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (debug) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }

        val pinner = CertificatePinner.Builder().add(
            GITHUB_URL,
            "sha256/m41PSCmB5CaR0rKh7VMMXQbDFgCNFXchcoNFm3RuoXw="
        ).add(
            GITHUB_URL,
            "sha256/k2v657xBsOVe1PQRwOsHsw3bsGT2VzIqz5K+59sNQws="
        ).add(
            GITHUB_URL,
            "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18="
        ).build()
        builder.certificatePinner(pinner)

        return builder.build()
    }

    @CheckResult
    private fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder().baseUrl(
            CURRENT_VERSION_REPO_BASE_URL
        ).client(
            okHttpClient
        ).addConverterFactory(
            GsonConverterFactory.create(gson)
        ).addCallAdapterFactory(
            RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread())
        ).build()
    }

    @CheckResult
    fun getPresenter(packageName: String, currentVersion: Int): VersionCheckPresenter {
        return VersionCheckPresenter(
            packageName, currentVersion,
            cachedInteractor,
            computationScheduler, ioScheduler, mainThreadScheduler
        )
    }

    companion object {

        private const val GITHUB_URL = "raw.githubusercontent.com"
        private const val CURRENT_VERSION_REPO_BASE_URL =
            "https://$GITHUB_URL/pyamsoft/android-project-versions/master/"
    }
}
