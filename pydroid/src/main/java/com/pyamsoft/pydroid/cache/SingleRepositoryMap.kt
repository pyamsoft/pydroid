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

interface RepositoryMap<in K : Any, V : Any> : Cache {

  @CheckResult
  fun get(
    bypass: Boolean,
    key: K,
    fresh: () -> Single<V>
  ): Single<V>

  @CheckResult
  fun get(
    bypass: Boolean,
    key: K,
    timeout: Long,
    fresh: () -> Single<V>
  ): Single<V>

  fun remove(key: K)

}

internal class RepositoryMapImpl<in K : Any, V : Any> internal constructor(
  private val provideScheduler: () -> Scheduler
) : RepositoryMap<K, V> {

  private val cache = LinkedHashMap<K, Repository<V>>()

  override fun clearCache() {
    cache.clear()
  }

  @CheckResult
  private fun get(key: K): Repository<V> = cache.getOrElse(key) { newRepository(provideScheduler) }

  @CheckResult
  override fun get(
    bypass: Boolean,
    key: K,
    fresh: () -> Single<V>
  ): Single<V> {
    return get(key).get(bypass, fresh)
  }

  override fun get(
    bypass: Boolean,
    key: K,
    timeout: Long,
    fresh: () -> Single<V>
  ): Single<V> {
    return get(key).get(bypass, timeout, fresh)
  }

  override fun remove(key: K) {
    cache.remove(key)
  }

}

@CheckResult
@JvmOverloads
fun <K : Any, V : Any> newRepositoryMap(
  scheduler: () -> Scheduler = { Schedulers.io() }
): RepositoryMap<K, V> {
  return RepositoryMapImpl(scheduler)
}

