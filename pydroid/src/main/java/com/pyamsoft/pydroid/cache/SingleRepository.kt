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

package com.pyamsoft.pydroid.cache

import android.support.annotation.CheckResult
import io.reactivex.Maybe

interface SingleRepository<T : Any> : CacheRepository<Maybe<T>> {

  fun set(data: T)
}

internal class SingleRepositoryImpl<T : Any> internal constructor() : SingleRepository<T> {

  private var data: T? = null
  private var time: Long = 0

  override fun clearCache() {
    data = null
    time = 0
  }

  @CheckResult
  override fun get(bypass: Boolean): Maybe<T> {
    return Maybe.defer<T> {
      if (bypass || data == null || time + THIRTY_SECONDS_MILLIS < System.currentTimeMillis()) {
        return@defer Maybe.empty()
      } else {
        return@defer Maybe.just(data)
      }
    }
  }

  override fun set(data: T) {
    this.data = data
    time = System.currentTimeMillis()
  }
}

