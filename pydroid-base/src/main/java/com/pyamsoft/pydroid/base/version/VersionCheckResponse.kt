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

import androidx.annotation.CheckResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Collections

@JsonClass(generateAdapter = true)
internal data class VersionCheckResponse internal constructor(
  @field:Json(name = "response_objects")
  internal val responseObjects: List<VersionCheckResponseEntry>?
) {

  @CheckResult
  fun responseObjects(): List<VersionCheckResponseEntry> {
    return responseObjects.let {
      if (it == null) {
        throw RuntimeException("VersionCheckResponse: responseObjects was null")
      } else {
        return@let Collections.unmodifiableList(it)
      }
    }
  }

  companion object

}
