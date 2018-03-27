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
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

interface Repository<T : Any> : Cache {

  @CheckResult
  fun get(fresh: () -> Single<T>): Single<T>

  @CheckResult
  fun get(
    bypass: Boolean,
    fresh: () -> Single<T>
  ): Single<T>
}

internal class RepositoryImpl<T : Any> internal constructor(
  private val ttl: Long,
  private val provideScheduler: () -> Scheduler
) : Repository<T> {

  private var data: Subject<T>? = null
  private var time = AtomicLong(0)

  override fun clearCache() {
    data = null
    time.set(0)
  }

  @CheckResult
  private fun getFreshOrCached(
    force: Boolean,
    currentTime: Long,
    fresh: () -> Single<T>
  ): Single<T> {
    return Single.defer {
      if (force) {
        // Time is atomic and should stop re-entry off multiple threads
        time.set(currentTime)

        // Make a new thread safe subject and use it
        data = AsyncSubject.create<T>()
            .toSerialized()

        // We subscribe indirectly and push onto the subject
        // so that the actual consumer subscribes to a source
        // which is un-opinionated about the Schedulers
        val scheduler = provideScheduler()
        fresh().subscribeOn(scheduler)
            .observeOn(scheduler)
            .subscribe(object : SingleObserver<T> {

              private val dispatch = data!!

              override fun onSubscribe(d: Disposable) {
                dispatch.onSubscribe(d)
              }

              override fun onSuccess(t: T) {
                dispatch.also {
                  it.onNext(t)
                  it.onComplete()
                }
              }

              override fun onError(e: Throwable) {
                dispatch.onError(e)
              }

            })
      }

      return@defer data!!.firstOrError()
    }
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
    return Single.defer {
      val currentTime = System.currentTimeMillis()
      val shouldForce = bypass || time.get() + ttl < currentTime
      return@defer getFreshOrCached(shouldForce, currentTime, fresh)
    }
  }
}

@CheckResult
@JvmOverloads
fun <T : Any> newRepository(
  ttl: Long = THIRTY_SECONDS_MILLIS,
  scheduler: () -> Scheduler = { Schedulers.io() }
): Repository<T> {
  return RepositoryImpl(ttl, scheduler)
}

@JvmField internal val THIRTY_SECONDS_MILLIS = TimeUnit.SECONDS.toMillis(30L)
