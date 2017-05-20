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
 *
 */

package com.pyamsoft.pydroid.version;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pyamsoft.pydroid.PYDroidModule;
import io.reactivex.Scheduler;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@RestrictTo(RestrictTo.Scope.LIBRARY) public class VersionCheckModule {

  @NonNull private static final String GITHUB_URL = "raw.githubusercontent.com";
  @NonNull private static final String CURRENT_VERSION_REPO_BASE_URL =
      "https://" + GITHUB_URL + "/pyamsoft/android-project-versions/master/";
  @NonNull private final VersionCheckInteractor interactor;
  @NonNull private final Scheduler obsScheduler;
  @NonNull private final Scheduler subScheduler;

  public VersionCheckModule(@NonNull PYDroidModule pyDroidModule) {
    obsScheduler = pyDroidModule.provideObsScheduler();
    subScheduler = pyDroidModule.provideSubScheduler();
    interactor = new VersionCheckInteractor(new VersionCheckApi(
        provideRetrofit(provideOkHttpClient(pyDroidModule.isDebug()), provideGson())).create(
        VersionCheckService.class));
  }

  @CheckResult @NonNull private Gson provideGson() {
    final GsonBuilder gsonBuilder =
        new GsonBuilder().registerTypeAdapterFactory(AutoValueTypeAdapterFactory.create());
    return gsonBuilder.create();
  }

  @CheckResult @NonNull private OkHttpClient provideOkHttpClient(boolean debug) {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
    if (debug) {
      final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addInterceptor(logging);
    }

    final CertificatePinner pinner = new CertificatePinner.Builder().add(GITHUB_URL,
        "sha256/m41PSCmB5CaR0rKh7VMMXQbDFgCNFXchcoNFm3RuoXw=")
        .add(GITHUB_URL, "sha256/k2v657xBsOVe1PQRwOsHsw3bsGT2VzIqz5K+59sNQws=")
        .add(GITHUB_URL, "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=")
        .build();
    builder.certificatePinner(pinner);

    return builder.build();
  }

  @CheckResult @NonNull
  private Retrofit provideRetrofit(@NonNull OkHttpClient okHttpClient, @NonNull Gson gson) {
    return new Retrofit.Builder().baseUrl(CURRENT_VERSION_REPO_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(subScheduler))
        .build();
  }

  @NonNull @CheckResult public VersionCheckPresenter getPresenter() {
    return new VersionCheckPresenter(interactor, obsScheduler, subScheduler);
  }
}
