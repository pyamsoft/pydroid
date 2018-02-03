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
