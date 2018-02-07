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

package com.pyamsoft.pydroid.base.version

import android.support.annotation.CheckResult
import com.google.auto.value.AutoValue
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName

@AutoValue
internal abstract class VersionCheckResponse internal constructor() {

  @CheckResult
  @SerializedName("response_objects")
  abstract fun responseObjects(): List<ResponseObject>

  @AutoValue
  internal abstract class ResponseObject internal constructor() {

    @CheckResult
    @SerializedName("min_api")
    abstract fun minApi(): Int

    @CheckResult
    abstract fun version(): Int

    companion object {

      @JvmStatic
      @CheckResult
      fun typeAdapter(gson: Gson): TypeAdapter<ResponseObject> =
        AutoValue_VersionCheckResponse_ResponseObject.GsonTypeAdapter(gson)
            .setDefaultMinApi(0).setDefaultVersion(0)
    }
  }

  companion object {

    @JvmStatic
    @CheckResult
    fun typeAdapter(gson: Gson): TypeAdapter<VersionCheckResponse> =
      AutoValue_VersionCheckResponse.GsonTypeAdapter(gson).setDefaultResponseObjects(
          emptyList()
      )
  }
}
