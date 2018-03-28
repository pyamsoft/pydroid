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
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

interface Repository<T : Any> : Cache {

  @CheckResult
  fun get(fresh: () -> Single<T>): Single<T>

  @CheckResult
  fun get(
    bypass: Boolean,
    fresh: () -> Single<T>
  ): Single<T>
}

internal class RepositoryImpl<T : Any> internal constructor(
  private val ttl: Long,
  private val provideScheduler: () -> Scheduler
) : Repository<T> {

  private var data = ConcurrentHashMap<Int, Single<T>?>(1)
  private var time: Long = 0

  override fun clearCache() {
    data.clear()
    time = 0
  }

  @CheckResult
  override fun get(fresh: () -> Single<T>): Single<T> {
    return get(false, fresh)
  }

  @CheckResult
  override fun get(
    bypass: Boolean,
    fresh: () -> Single<T>
  ): Single<T> {
    return Single.defer {
      val currentTime = System.currentTimeMillis()

      // If we need to force a refresh, clear the cache
      if (bypass || time + ttl < currentTime) {
        clearCache()
      }

      // If we have a cached entry return it
      var single: Single<T>? = data[0]
      if (single != null) {
        return@defer single
      }

      // Make new data and store it for later
      time = currentTime
      single = fresh().cache()

      // If someone has already put data in, use it
      val cached: Single<T>? = data.putIfAbsent(0, single)
      if (cached != null) {
        return@defer cached
      }

      return@defer single
    }
  }
}

@CheckResult
@JvmOverloads
fun <T : Any> repository(
  time: Long = 30L,
  timeUnit: TimeUnit = TimeUnit.SECONDS,
  scheduler: () -> Scheduler = { Schedulers.io() }
): Repository<T> {
  return RepositoryImpl(timeUnit.toMillis(time), scheduler)
}

