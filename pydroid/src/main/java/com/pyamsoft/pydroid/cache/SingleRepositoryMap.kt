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

interface SingleRepositoryMap<in K : Any, V : Any> : CacheRepositoryMap<K, V, Maybe<V>> {

  fun set(
    key: K,
    data: V
  )
}

internal class SingleRepositoryMapImpl<in K : Any, V : Any> internal constructor(
) : SingleRepositoryMap<K, V>, Cache {

  private val cache = LinkedHashMap<K, SingleRepository<V>>()

  override fun clearCache() {
    cache.clear()
  }

  @CheckResult
  private fun get(key: K): SingleRepository<V> = cache.getOrElse(key) { cacheSingle() }

  @CheckResult
  override fun get(
    bypass: Boolean,
    key: K
  ): Maybe<V> {
    return Maybe.defer<V> {
      return@defer get(key).get(bypass)
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

