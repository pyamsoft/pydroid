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
import io.reactivex.Single
import java.util.concurrent.TimeUnit

interface Repository<T : Any> : Cache {

  @CheckResult
  fun get(
    bypass: Boolean,
    fresh: () -> Single<T>
  ): Single<T>

  @CheckResult
  fun get(
    bypass: Boolean,
    timeout: Long,
    fresh: () -> Single<T>
  ): Single<T>
}

internal class RepositoryImpl<T : Any> internal constructor() : Repository<T> {

  private var data: T? = null
  private var time: Long = 0

  override fun clearCache() {
    set(null, 0)
  }

  @CheckResult
  override fun get(
    bypass: Boolean,
    fresh: () -> Single<T>
  ): Single<T> {
    return get(bypass, THIRTY_SECONDS_MILLIS, fresh)
  }

  @CheckResult
  private fun getFromDataCache(
    bypass: Boolean,
    timeout: Long
  ): Maybe<T> {
    return Maybe.defer<T> {
      if (bypass || data == null || time + timeout < System.currentTimeMillis()) {
        // Clear data cache if exists
        set(null, 0)
        return@defer Maybe.empty()
      } else {
        return@defer Maybe.just(data)
      }
    }
  }

  @CheckResult
  override fun get(
    bypass: Boolean,
    timeout: Long,
    fresh: () -> Single<T>
  ): Single<T> {
    return Single.defer {
      Maybe.concat(
          getFromDataCache(bypass, timeout),
          fresh().doOnSuccess { set(it, System.currentTimeMillis()) }.toMaybe()
      )
          .firstOrError()
    }
  }

  private fun set(
    data: T?,
    time: Long
  ) {
    this.data = data
    this.time = time
  }

  companion object {

    private val THIRTY_SECONDS_MILLIS = TimeUnit.SECONDS.toMillis(30L)
  }
}

@CheckResult
fun <T : Any> newRepository(): Repository<T> {
  return RepositoryImpl()
}
