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

interface EventConsumer<T : Any> {

  suspend fun onEvent(emitter: suspend (event: T) -> Unit)

  companion object {

    @CheckResult
    fun <T : Any> create(
      from: suspend (
        onCancel: (doOnCancel: () -> Unit) -> Unit,
        startWith: (doOnStart: () -> T) -> Unit,
        emit: (event: T) -> Unit
      ) -> Unit
    ): EventConsumer<T> {
      return object : EventConsumer<T> {

        private val realBus = EventBus.create<T>()

        override suspend fun onEvent(emitter: suspend (event: T) -> Unit) {
          // If the caller uses onCancel { } it will populate cancel as a lambda
          var cancel: (() -> Unit)? = null
          val onCancel: (doOnCancel: () -> Unit) -> Unit = { cancel = it }

          // On emit, we publish on the bus
          val onEmit: (value: T) -> Unit = { realBus.publish(it) }

          // Run and emit an event on stream start
          var startWith: (() -> T)? = null
          val onStart: (doOnStart: () -> T) -> Unit = { startWith = it }

          from(onCancel, onStart, onEmit)

          // Start with using the emitter because the bus will not be listening yet
          val start = startWith
          if (start != null) {
            emitter(start())
          }

          realBus.onEvent { emitter(it) }

          val end = cancel
          if (end != null) {
            end()
          }
        }

      }
    }
  }
}
