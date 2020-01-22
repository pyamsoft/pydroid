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
import kotlinx.coroutines.Job
import kotlin.coroutines.coroutineContext

interface EventConsumer<T : Any> {

    suspend fun onEvent(emitter: suspend (event: T) -> Unit)

    companion object {

        @CheckResult
        fun <T : Any> fromCallback(
            callback: suspend (
                onCancel: (doOnCancel: () -> Unit) -> Unit,
                startWith: (doOnStart: () -> T) -> Unit,
                emit: (event: T) -> Unit
            ) -> Unit
        ): EventConsumer<T> {
            return object : EventConsumer<T> {

                private val realBus = EventBus.create<T>()

                private var cancel: (() -> Unit)? = null
                private var startWith: (() -> T)? = null

                private fun onCancel(doOnCancel: () -> Unit) {
                    cancel = doOnCancel
                }

                private fun onEmit(value: T) {
                    realBus.publish(value)
                }

                // Run and emit an event on stream start
                private fun onStart(doOnStart: () -> T) {
                    startWith = doOnStart
                }

                override suspend fun onEvent(emitter: suspend (event: T) -> Unit) {
                    val self = this

                    requireNotNull(coroutineContext[Job]).invokeOnCompletion {
                        val end = cancel
                        if (end != null) {
                            end()
                        }
                    }

                    callback(this::onCancel, self::onStart, self::onEmit)

                    // Start with using the emitter because the bus will not be listening yet
                    val start = startWith
                    if (start != null) {
                        emitter(start())
                    }

                    realBus.onEvent { emitter(it) }
                }
            }
        }
    }
}
