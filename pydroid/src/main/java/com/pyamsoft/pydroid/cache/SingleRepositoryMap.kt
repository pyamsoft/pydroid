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

interface RepositoryMap<in K : Any, V : Any> : Cache {

  @CheckResult
  fun get(
    bypass: Boolean,
    key: K
  ): Maybe<V>

  @CheckResult
  fun get(
    bypass: Boolean,
    key: K,
    timeout: Long
  ): Maybe<V>

  fun remove(key: K)

  fun set(
    key: K,
    data: V
  )
}

internal class RepositoryMapImpl<in K : Any, V : Any> internal constructor(
) : RepositoryMap<K, V> {

  private val cache = LinkedHashMap<K, Repository<V>>()

  override fun clearCache() {
    cache.clear()
  }

  @CheckResult
  private fun get(key: K): Repository<V> = cache.getOrElse(key) { newRepository() }

  @CheckResult
  override fun get(
    bypass: Boolean,
    key: K
  ): Maybe<V> {
    return Maybe.defer<V> {
      return@defer get(key).get(bypass)
    }
  }

  override fun get(
    bypass: Boolean,
    key: K,
    timeout: Long
  ): Maybe<V> {
    return Maybe.defer<V> {
      return@defer get(key).get(bypass, timeout)
    }
  }

  override fun set(
    key: K,
    data: V
  ) {
    get(key).set(data)
  }

  override fun remove(key: K) {
    cache.remove(key)
  }

}

@CheckResult
fun <K : Any, V : Any> newRepositoryMap(): RepositoryMap<K, V> {
  return RepositoryMapImpl()
}

