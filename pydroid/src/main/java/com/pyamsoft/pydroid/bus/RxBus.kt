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

package com.pyamsoft.pydroid.bus

import android.support.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

class RxBus<T : Any> private constructor() : EventBus<T> {

  private val bus: Subject<T> = PublishSubject.create<T>()
      .toSerialized()

  override fun publish(event: T) {
    if (bus.hasObservers()) {
      bus.onNext(event)
    } else {
      Timber.w("No observers on bus, ignore publish event: %s", event)
    }
  }

  override fun listen(): Observable<T> = bus

  companion object {

    /**
     * Create a new local bus instance to use
     */
    @JvmStatic
    @CheckResult
    fun <T : Any> create(): EventBus<T> = RxBus()
  }
}
