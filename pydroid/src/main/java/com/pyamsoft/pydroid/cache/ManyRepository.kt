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

interface ManyRepository<T : Any> : CacheRepository<Observable<T>> {

  fun prepare()

  fun add(data: T)

}

internal class ManyRepositoryImpl<T : Any> internal constructor() : ManyRepository<T> {

  private var data: MutableList<T>? = null
  private var time: Long = 0

  override fun clearCache() {
    data = null
    time = 0
  }

  @CheckResult
  override fun get(bypass: Boolean): Observable<T> {
    return Observable.defer<T> {
      val cached = data
      if (bypass || cached == null || cached.isEmpty() || time + THIRTY_SECONDS_MILLIS < System.currentTimeMillis()) {
        return@defer Observable.empty()
      } else {
        return@defer Observable.fromIterable(cached)
      }
    }
  }

  override fun prepare() {
    this.data = ArrayList()
    time = 0
  }

  override fun add(data: T) {
    this.data.also {
      if (it == null) {
        throw IllegalStateException("Must prepare Repository by calling prepare() before add()")
      } else {
        it.add(data)
        time = System.currentTimeMillis()
      }
    }
  }
}

