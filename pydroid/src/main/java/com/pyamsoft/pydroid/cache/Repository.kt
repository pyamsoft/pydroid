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
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
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

internal class RepositoryImpl<T : Any> internal constructor(
  private val provideScheduler: () -> Scheduler
) : Repository<T> {

  private var data: AtomicReference<Subject<T>?> = AtomicReference(null)
  private var time: Long = 0

  override fun clearCache() {
    data.set(null)
    time = 0
  }

  @CheckResult
  private fun getFreshOrCached(
    force: Boolean,
    currentTime: Long,
    fresh: () -> Single<T>
  ): Single<T> {
    return Observable.defer {
      val cachedSubject = data.get()
      if (cachedSubject == null || force) {
        val asyncSubject = AsyncSubject.create<T>()
        val scheduler = provideScheduler()
        fresh().toObservable()
            .subscribeOn(scheduler)
            .observeOn(scheduler)
            .subscribe(asyncSubject)

        // Cache for later
        data.set(asyncSubject)
        time = currentTime

        return@defer asyncSubject
      } else {
        return@defer cachedSubject
      }
    }
        .firstOrError()
  }

  @CheckResult
  override fun get(
    bypass: Boolean,
    fresh: () -> Single<T>
  ): Single<T> {
    return get(bypass, THIRTY_SECONDS_MILLIS, fresh)
  }

  @CheckResult
  override fun get(
    bypass: Boolean,
    timeout: Long,
    fresh: () -> Single<T>
  ): Single<T> {
    return Single.defer {
      val currentTime = System.currentTimeMillis()
      val shouldForce = bypass || data.get() == null || time + timeout < currentTime
      return@defer getFreshOrCached(shouldForce, currentTime, fresh)
    }
  }

  companion object {

    private val THIRTY_SECONDS_MILLIS = TimeUnit.SECONDS.toMillis(30L)
  }
}

@CheckResult
@JvmOverloads
fun <T : Any> newRepository(
  scheduler: () -> Scheduler = { Schedulers.io() }
): Repository<T> {
  return RepositoryImpl(scheduler)
}
