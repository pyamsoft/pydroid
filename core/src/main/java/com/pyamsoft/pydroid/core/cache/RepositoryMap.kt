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

package com.pyamsoft.pydroid.core.cache

import androidx.annotation.CheckResult
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

interface RepositoryMap<in K : Any, V : Any> : Cache {

  @CheckResult
  fun get(
    key: K,
    fresh: () -> Single<V>
  ): Single<V>

  @CheckResult
  fun get(
    bypass: Boolean,
    key: K,
    fresh: () -> Single<V>
  ): Single<V>

  fun clearKey(key: K)

}

internal class RepositoryMapImpl<in K : Any, V : Any> internal constructor(
  initialSize: Int,
  private val time: Long,
  private val timeUnit: TimeUnit,
  private val provideScheduler: () -> Scheduler
) : RepositoryMap<K, V> {

  private val cache = ConcurrentHashMap<K, Repository<V>>(initialSize)

  override fun clearCache() {
    cache.clear()
  }

  @CheckResult
  private fun get(key: K): Repository<V> =
    cache.getOrElse(key) {
      repository(
          time, timeUnit, provideScheduler
      )
    }

  @CheckResult
  override fun get(
    key: K,
    fresh: () -> Single<V>
  ): Single<V> {
    return get(key).get(fresh)
  }

  override fun get(
    bypass: Boolean,
    key: K,
    fresh: () -> Single<V>
  ): Single<V> {
    return get(key).get(bypass, fresh)
  }

  override fun clearKey(key: K) {
    cache.remove(key)
  }

}

@CheckResult
@JvmOverloads
fun <K : Any, V : Any> repositoryMap(
  initialSize: Int = 16,
  time: Long = 30L,
  timeUnit: TimeUnit = TimeUnit.SECONDS,
  provideScheduler: () -> Scheduler = { Schedulers.io() }
): RepositoryMap<K, V> {
  return RepositoryMapImpl(
      initialSize, time, timeUnit, provideScheduler
  )
}

