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

package com.pyamsoft.pydroid.arch

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.bus.EventBus
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class UiEventHandler<E : Any, C : Any> protected constructor(
  private val bus: EventBus<E>
) {

  protected fun publish(event: E) {
    bus.publish(event)
  }

  @CheckResult
  protected fun listen(): Observable<E> {
    return bus.listen()
  }

  @CheckResult
  abstract fun handle(delegate: C): Disposable

}