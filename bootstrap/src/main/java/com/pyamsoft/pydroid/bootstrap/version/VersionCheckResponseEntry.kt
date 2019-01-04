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

import androidx.annotation.CheckResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class VersionCheckResponseEntry internal constructor(
  @field:Json(name = "min_api")
  internal val minApi: Int,
  internal val version: Int
) {

  @CheckResult
  fun minApi(): Int {
    return minApi.let {
      if (it == 0) {
        throw RuntimeException("ResponseObject: minApi was 0")
      } else {
        return@let it
      }
    }
  }

  @CheckResult
  fun version(): Int {
    return version.let {
      if (it == 0) {
        throw RuntimeException("ResponseObject: version was 0")
      } else {
        return@let it
      }
    }
  }

  // Needed so we can generate a static adapter
  companion object
}