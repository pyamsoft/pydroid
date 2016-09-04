/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.licensecheck;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pyamsoft.pydroid.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module public class ApiModule {

  private final boolean isDebugMode;
  @NonNull private final String projectName;

  public ApiModule(boolean isDebugMode, @NonNull String projectName) {
    this.isDebugMode = isDebugMode;
    this.projectName = projectName;
  }

  @ActivityScope @Provides Gson provideGson() {
    final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(
        new LicenseCheckApi.AutoValueTypeAdapterFactory());
    return gsonBuilder.create();
  }

  @ActivityScope @Provides OkHttpClient provideOkHttpClient() {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
    if (isDebugMode) {
      final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addInterceptor(logging);
    }
    return builder.build();
  }

  @ActivityScope @Provides Retrofit provideRetrofit(final Gson gson, final OkHttpClient client) {
    // TODO change to master
    final String url =
        String.format(Locale.getDefault(), "https://raw.githubusercontent.com/pyamsoft/%s/dev/",
            projectName);
    return new Retrofit.Builder().baseUrl(url)
        .client(client)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
  }

  @ActivityScope @Provides LicenseCheckApi provideLicenseCheckApi(@NonNull Retrofit retrofit) {
    return new GithubLicenseCheckApi(retrofit);
  }
}
