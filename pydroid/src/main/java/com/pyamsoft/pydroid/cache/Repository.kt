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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

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

  private var subject: AtomicReference<Subject<T>?> = AtomicReference(null)
  private var data: T? = null
  private var time: Long = 0

  override fun clearCache() {
    set(null, 0)
    subject.set(null)
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
  private fun persistFreshRequestUntilComplete(fresh: () -> Single<T>): Single<T> {
    return Observable.defer {
      val freshCache = subject.get()
      if (freshCache != null) {
        return@defer freshCache
      } else {
        return@defer AsyncSubject.create<T>()
            .also {
              fresh().toObservable()
                  .subscribe(it)
              subject.set(it)
            }
      }
    }
        .firstOrError()
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
          persistFreshRequestUntilComplete(fresh).doOnSuccess {
            set(it, System.currentTimeMillis())
          }.toMaybe()
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
    this.subject.set(null)
  }

  companion object {

    private val THIRTY_SECONDS_MILLIS = TimeUnit.SECONDS.toMillis(30L)
  }
}

@CheckResult
fun <T : Any> newRepository(): Repository<T> {
  return RepositoryImpl()
}
