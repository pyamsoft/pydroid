/*
 * Copyright 2019 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.pydroid.core.bus

import androidx.annotation.CheckResult
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asObservable

internal class RealBus<T : Any> private constructor(private val scope: CoroutineScope) : EventBus<T> {

  @ExperimentalCoroutinesApi
  private val bus by lazy { BroadcastChannel<T>(1) }

  @ExperimentalCoroutinesApi
  override fun publish(event: T) {
    scope.launch { bus.send(event) }
  }

  @ObsoleteCoroutinesApi
  @ExperimentalCoroutinesApi
  override fun listen(): Observable<T> {
    return bus.openSubscription()
        .asObservable(scope.coroutineContext)
  }

  companion object {

    private val EMPTY by lazy { create<Unit>(GlobalScope) }

    /**
     * Create a new local viewBus instance to use
     */
    @JvmStatic
    @CheckResult
    fun <T : Any> create(scope: CoroutineScope): EventBus<T> = RealBus(scope)

    @JvmStatic
    @CheckResult
    fun empty(): EventBus<Unit> = EMPTY
  }
}
