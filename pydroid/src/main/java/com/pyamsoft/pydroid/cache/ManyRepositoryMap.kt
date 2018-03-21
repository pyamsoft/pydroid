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
import io.reactivex.Observable

interface ManyRepositoryMap<in K : Any, V : Any> : CacheRepositoryMap<K, V, Observable<V>> {

  fun prepare(key: K)

  fun add(
    key: K,
    value: V
  )

}

internal class ManyRepositoryMapImpl<in K : Any, V : Any> internal constructor(
) : ManyRepositoryMap<K, V>, Cache {

  private val cache = LinkedHashMap<K, ManyRepository<V>>()

  override fun clearCache() {
    cache.clear()
  }

  @CheckResult
  private fun get(key: K): ManyRepository<V> = cache.getOrElse(key) { cacheMany() }

  @CheckResult
  override fun get(
    bypass: Boolean,
    key: K
  ): Observable<V> {
    return Observable.defer<V> {
      return@defer get(key).get(bypass)
    }
  }

  override fun prepare(key: K) {
    get(key).prepare()
  }

  override fun add(
    key: K,
    value: V
  ) {
    get(key).add(value)
  }

  override fun remove(key: K) {
    cache.remove(key)
  }

}
