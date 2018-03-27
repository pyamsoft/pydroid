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
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.AsyncSubject
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

interface MutableRepository<T : Any> : Repository<T> {

  fun set(value: T)

  fun update(func: (T) -> T)
}

internal class RepositoryImpl<T : Any> internal constructor(
  private val ttl: Long,
  private val provideScheduler: () -> Scheduler
) : MutableRepository<T> {

  private var data = ConcurrentHashMap<Int, AsyncSubject<T>?>(1)
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
    return Observable.defer {
      val currentTime = System.currentTimeMillis()

      // If we need to force a refresh, clear the cache
      if (bypass || time + ttl < currentTime) {
        clearCache()
      }

      // If we have a cached entry return it
      var subject: AsyncSubject<T>? = data[0]
      if (subject != null) {
        return@defer subject
      }

      // Make new data and store it for later
      time = currentTime
      subject = AsyncSubject.create<T>()

      // If someone has already put data in, use it
      val cached: AsyncSubject<T>? = data.putIfAbsent(0, subject)
      if (cached != null) {
        return@defer cached
      }

      // We subscribe indirectly and push onto the subject
      // so that the actual consumer subscribes to a source
      // which is un-opinionated about the Schedulers
      val scheduler = provideScheduler()
      fresh().subscribeOn(scheduler)
          .observeOn(scheduler)
          .subscribe(object : SingleObserver<T> {

            override fun onSubscribe(d: Disposable) {
              subject.onSubscribe(d)
            }

            override fun onSuccess(t: T) {
              subject.also {
                it.onNext(t)
                it.onComplete()
              }
            }

            override fun onError(e: Throwable) {
              subject.onError(e)
            }

          })

      return@defer subject
    }
        .singleOrError()
  }

  private fun set(
    value: T,
    newTime: Long
  ) {
    clearCache()
    data[0] = AsyncSubject.create<T>()
        .also {
          it.onNext(value)
          it.onComplete()
        }
    time = newTime
  }

  override fun set(value: T) {
    set(value, System.currentTimeMillis())
  }

  override fun update(func: (T) -> T) {
    val subject: AsyncSubject<T>? = data[0]
    if (subject != null) {
      val value: T? = subject.value
      if (value != null) {
        set(func(value), time)
      }
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
  return mutableRepository(time, timeUnit, scheduler)
}

@CheckResult
@JvmOverloads
fun <T : Any> mutableRepository(
  time: Long = 30L,
  timeUnit: TimeUnit = TimeUnit.SECONDS,
  scheduler: () -> Scheduler = { Schedulers.io() }
): MutableRepository<T> {
  return RepositoryImpl(timeUnit.toMillis(time), scheduler)
}
