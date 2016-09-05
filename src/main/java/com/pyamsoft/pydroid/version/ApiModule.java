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

package com.pyamsoft.pydroid.version;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pyamsoft.pydroid.BuildConfig;
import com.pyamsoft.pydroid.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module public class ApiModule {

  @NonNull private static final String CURRENT_VERSION_REPO_BASE_URL =
      "https://raw.githubusercontent.com/pyamsoft/android-project-versions/master/";

  public ApiModule() {
  }

  @ActivityScope @Provides Gson provideGson() {
    final GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapterFactory(
        new VersionCheckApi.AutoValueTypeAdapterFactory());
    return gsonBuilder.create();
  }

  @ActivityScope @Provides OkHttpClient provideOkHttpClient() {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
    if (BuildConfig.DEBUG) {
      final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addInterceptor(logging);
    }
    return builder.build();
  }

  @ActivityScope @Provides Retrofit provideRetrofit(final Gson gson, final OkHttpClient client) {
    return new Retrofit.Builder().baseUrl(CURRENT_VERSION_REPO_BASE_URL)
        .client(client)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
  }

  @ActivityScope @Provides VersionCheckApi provideLicenseCheckApi(@NonNull Retrofit retrofit) {
    return new GithubVersionCheckApi(retrofit);
  }
}
