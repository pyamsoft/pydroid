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

import com.pyamsoft.pydroid.cache.Cache
import com.pyamsoft.pydroid.cache.CacheTimeout
import com.pyamsoft.pydroid.cache.TimedEntry
import io.reactivex.Single

internal class VersionCheckInteractorCache internal constructor(
  private val impl: VersionCheckInteractor
) : VersionCheckInteractor, Cache {

  private val cacheTimeout = CacheTimeout(this)
  private val cachedResponse = TimedEntry<Single<Int>>()

  override fun checkVersion(
    force: Boolean,
    packageName: String
  ): Single<Int> {
    return cachedResponse.getElseFresh(force) {
      impl.checkVersion(true, packageName)
          .cache()
    }
        .doOnError { clearCache() }
        .doAfterTerminate { cacheTimeout.queue() }
  }

  override fun clearCache() {
    cachedResponse.clearCache()
    cacheTimeout.reset()
  }
}
