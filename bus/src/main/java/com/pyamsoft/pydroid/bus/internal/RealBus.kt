/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bus.internal

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bus.EventBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Real implementation of the EventBus
 */
internal class RealBus<T : Any> internal constructor(
    private val emitOnlyWhenActive: Boolean,
    private val replayCount: Int,
    private val context: CoroutineContext
) : EventBus<T> {

    // Backing bus
    private val bus by lazy { MutableSharedFlow<T>(replay = replayCount) }

    // Keep around items which have not been emitted yet because of no active subscribers
    //
    // As far as I know, we can't use any of the shared flow built in behaviors
    // because we want a queue that replays all items to only the first subscriber.
    private val mutex = Mutex()
    private val waitingQueue by lazy { mutableListOf<T>() }

    @CheckResult
    private fun isBusReady(): Boolean {
        return bus.subscriptionCount.value > 0
    }

    private suspend inline fun withQueue(func: MutableList<T>.() -> Unit): Unit = mutex.withLock {
        func(waitingQueue)
    }

    private suspend fun publish(event: T) {
        bus.emit(event)
    }

    private suspend fun sendOrQueue(event: T) {
        if (isBusReady()) {
            publish(event)
        } else {
            withQueue { add(event) }
        }
    }

    private suspend inline fun emitQueuedEvents(emitter: (event: T) -> Unit) {
        if (!emitOnlyWhenActive) {
            return
        }

        withQueue {
            while (isNotEmpty()) {
                val pastEvent = removeAt(0)
                emitter(pastEvent)
            }
        }
    }

    override suspend fun send(event: T) = withContext(context) {
        if (emitOnlyWhenActive) {
            sendOrQueue(event)
        } else {
            publish(event)
        }
    }

    @CheckResult
    override suspend fun onEvent(emitter: suspend (event: T) -> Unit) = withContext(context) {
        bus.onSubscription {
            emitQueuedEvents { event ->
                this.emit(event)
            }
        }.collect(emitter)
    }
}
