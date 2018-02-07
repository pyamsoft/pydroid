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

import com.pyamsoft.pydroid.data.Cache
import io.reactivex.Single
import java.util.concurrent.TimeUnit

internal class VersionCheckInteractorCache internal constructor(
  private val impl: VersionCheckInteractor
) : VersionCheckInteractor, Cache {

  private var cachedResponse: Single<Int>? = null
  private var responseLastAccess: Long = 0L

  override fun checkVersion(
    packageName: String,
    force: Boolean
  ): Single<Int> {
    return Single.defer {
      val cache = cachedResponse
      val response: Single<Int>
      val currentTime = System.currentTimeMillis()
      if (force || cache == null || responseLastAccess + THIRTY_SECONDS_MILLIS < currentTime) {
        response = impl.checkVersion(packageName, force)
            .cache()
        cachedResponse = response
        responseLastAccess = currentTime
      } else {
        response = cache
      }
      return@defer response
    }
        .doOnError { clearCache() }
  }

  override fun clearCache() {
    cachedResponse = null
  }

  companion object {

    private val THIRTY_SECONDS_MILLIS = TimeUnit.SECONDS.toMillis(30L)
  }
}
