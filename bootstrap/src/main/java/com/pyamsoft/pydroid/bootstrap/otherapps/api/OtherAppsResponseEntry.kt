/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bootstrap.otherapps.api

import androidx.annotation.CheckResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class OtherAppsResponseEntry
internal constructor(
    @field:Json(name = "package") internal val packageName: String?,
    internal val name: String?,
    internal val description: String?,
    internal val icon: String?,
    @field:Json(name = "url") internal val storeUrl: String?,
    @field:Json(name = "source") internal val sourceUrl: String?
) {

  @CheckResult
  fun packageName(): String {
    return packageName.let {
      if (it == null) {
        throw RuntimeException("OtherAppsResponseEntry: packageName missing")
      } else {
        return@let it
      }
    }
  }

  @CheckResult
  fun name(): String {
    return name.let {
      if (it == null) {
        throw RuntimeException("OtherAppsResponseEntry: name missing")
      } else {
        return@let it
      }
    }
  }

  @CheckResult
  fun description(): String {
    return description.let {
      if (it == null) {
        throw RuntimeException("OtherAppsResponseEntry: description missing")
      } else {
        return@let it
      }
    }
  }

  @CheckResult
  fun icon(): String {
    return icon.let {
      if (it == null) {
        throw RuntimeException("OtherAppsResponseEntry: icon missing")
      } else {
        return@let it
      }
    }
  }

  @CheckResult
  fun url(): String {
    return storeUrl.let {
      if (it == null) {
        throw RuntimeException("OtherAppsResponseEntry: url missing")
      } else {
        return@let it
      }
    }
  }

  @CheckResult
  fun source(): String {
    return sourceUrl.let {
      if (it == null) {
        throw RuntimeException("OtherAppsResponseEntry: source missing")
      } else {
        return@let it
      }
    }
  }

  // Needed so we can generate a static adapter
  companion object
}
