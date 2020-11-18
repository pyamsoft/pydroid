/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.arch.internal

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.EventBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class RealBus<T : Any> internal constructor(
    private val emitOnlyWhenActive: Boolean,
    private val context: CoroutineContext
) : EventBus<T> {

    // Backing bus
    private val bus by lazy { MutableSharedFlow<T>() }

    // Keep around items which have not been emitted yet because of no active subscribers
    private val mutex = Mutex()
    private val waitingQueue by lazy { mutableListOf<T>() }

    @CheckResult
    private fun isBusReady(): Boolean {
        return bus.subscriptionCount.value > 0
    }

    private suspend fun sendOrQueue(event: T) {
        mutex.withLock {
            if (isBusReady()) {
                bus.emit(event)
            } else {
                waitingQueue.add(event)
            }
        }
    }

    override suspend fun send(event: T) {
        withContext(context) {
            if (emitOnlyWhenActive) {
                sendOrQueue(event)
            } else {
                bus.emit(event)
            }
        }
    }

    private suspend inline fun emitQueuedEvents(emitter: (event: T) -> Unit) {
        if (emitOnlyWhenActive) {
            mutex.withLock {
                waitingQueue.forEach(emitter)
                waitingQueue.clear()
            }
        }
    }

    @CheckResult
    override suspend fun onEvent(emitter: suspend (event: T) -> Unit) {
        withContext(context) {
            bus
                .onSubscription { emitQueuedEvents { emit(it) } }
                .collect(emitter)
        }
    }
}
