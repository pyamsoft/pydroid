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
 *
 */

package com.pyamsoft.pydroid.version;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.pyamsoft.pydroid.helper.Checker;
import retrofit2.Retrofit;

@RestrictTo(RestrictTo.Scope.LIBRARY) class VersionCheckApi {

  @NonNull private final Retrofit client;

  VersionCheckApi(@NonNull Retrofit client) {
    this.client = Checker.checkNonNull(client);
  }

  @NonNull @CheckResult public <T> T create(final Class<T> serviceClass) {
    return client.create(Checker.checkNonNull(serviceClass));
  }

  static final class AutoValueTypeAdapterFactory implements TypeAdapterFactory {

    @Nullable @CheckResult @SuppressWarnings("unchecked") @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      gson = Checker.checkNonNull(gson);
      type = Checker.checkNonNull(type);

      TypeAdapter<T> adapter;
      final Class<? super T> rawType = type.getRawType();
      if (rawType.equals(VersionCheckResponse.class)) {
        adapter = (TypeAdapter<T>) VersionCheckResponse.typeAdapter(gson);
      } else {
        adapter = null;
      }

      return adapter;
    }
  }
}
