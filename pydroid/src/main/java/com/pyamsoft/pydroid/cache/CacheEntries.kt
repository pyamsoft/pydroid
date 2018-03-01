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
import androidx.util.lruCache
import java.util.concurrent.TimeUnit

@JvmField
val DEFAULT_CACHE_TIMEOUT: Long = TimeUnit.MINUTES.toMillis(3L)

private data class TimedCacheEntry<out T : Any> internal constructor(
  internal val data: T,
  internal val accessTime: Long
)

class TimedEntry<T : Any> : Cache {

  private var cacheEntry: TimedCacheEntry<T>? = null

  override fun clearCache() {
    cacheEntry = null
  }

  fun updateIfAvailable(func: (T) -> T): TimedEntry<T> {
    val cached = cacheEntry
    if (cached != null) {
      cacheEntry = TimedCacheEntry(func(cached.data), cached.accessTime)
    }

    return this
  }

  @JvmOverloads
  @CheckResult
  fun getElseFresh(
    force: Boolean,
    timeout: Long = DEFAULT_CACHE_TIMEOUT,
    fresh: () -> T
  ): T {
    val result: T
    val cached: TimedCacheEntry<T>? = cacheEntry
    val currentTime = System.currentTimeMillis()
    if (force || cached == null || cached.accessTime + timeout < currentTime) {
      result = fresh()
      cacheEntry = TimedCacheEntry(result, currentTime)
    } else {
      result = cached.data
    }

    return result
  }
}

class TimedMap<in K : Any, V : Any>(size: Int = 5) : CacheMap<K, V> {

  private val cacheMap = lruCache<K, TimedCacheEntry<V>>(size)

  override fun clearCache() {
    cacheMap.evictAll()
  }

  override fun put(
    key: K,
    value: V
  ): CacheMap<K, V> {
    cacheMap.put(key, TimedCacheEntry(value, System.currentTimeMillis()))
    return this
  }

  override fun updateIfAvailable(
    key: K,
    func: (V) -> V
  ): CacheMap<K, V> {
    val cached = cacheMap.get(key)
    if (cached != null) {
      cacheMap.put(key, TimedCacheEntry(func(cached.data), cached.accessTime))
    }

    return this
  }

  override fun remove(key: K): V? = cacheMap.remove(key)?.data

  @JvmOverloads
  @CheckResult
  fun getElseFresh(
    force: Boolean,
    key: K,
    timeout: Long = DEFAULT_CACHE_TIMEOUT,
    fresh: () -> V
  ): V {
    val result: V
    val cached: TimedCacheEntry<V>? = cacheMap[key]
    val currentTime = System.currentTimeMillis()
    if (force || cached == null || cached.accessTime + timeout < currentTime) {
      result = fresh()
      cacheMap.put(key, TimedCacheEntry(result, currentTime))
    } else {
      result = cached.data
    }

    return result
  }
}
