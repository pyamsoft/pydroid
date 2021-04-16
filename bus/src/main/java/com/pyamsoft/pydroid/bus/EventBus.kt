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

package com.pyamsoft.pydroid.bus

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bus.internal.RealBus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A basic EventBus interface with send and receive methods
 */
public interface EventBus<T : Any> : EventConsumer<T> {

    /**
     * Emit an event to the event bus, suspend if needed by the implementation
     */
    public suspend fun send(event: T)

    public companion object {

        /**
         * The EventBus will event the event in the following cases
         *
         * If [emitOnlyWhenActive] is false, the event will always emit immediately
         * If [emitOnlyWhenActive] is true, the event will be emitted if/once a subscriber is listening
         * on the bus
         *
         * If [emitOnlyWhenActive] is true, and no subscribers are present for an event emission,
         * the event will be queued. Once a subscriber joins the bus, all subscribers will
         * then receive all events up to that point that were queued. Once a subscriber joins the
         * bus, events will always emit immediately.
         */
        @CheckResult
        @JvmStatic
        @JvmOverloads
        public fun <T : Any> create(
            emitOnlyWhenActive: Boolean,
            replayCount: Int = 0,
            context: CoroutineContext = EmptyCoroutineContext
        ): EventBus<T> = RealBus(emitOnlyWhenActive, replayCount, context)
    }
}
