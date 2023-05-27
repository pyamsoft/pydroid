/*
 * Copyright 2023 pyamsoft
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
import kotlinx.coroutines.flow.MutableSharedFlow

/** A basic EventBus interface with send and receive methods */
@Deprecated("Use SharedFlow directly instead")
public interface EventBus<T : Any> : EventConsumer<T> {

  /** Emit an event to the event bus, suspend if needed by the implementation */
  public suspend fun send(event: T)

  public companion object {

    /**
     * The EventBus will event the event in the following cases
     *
     * The EventBus is backed by a SharedFlow and follows it's implementation for behavior regarding
     * sending and collecting of events
     *
     * See [MutableSharedFlow.emit] and [MutableSharedFlow.collect]
     */
    @JvmStatic
    @CheckResult
    @JvmOverloads
    public fun <T : Any> create(
        replayCount: Int = 0,
        context: CoroutineContext = EmptyCoroutineContext
    ): EventBus<T> = RealBus(replayCount, context)
  }
}
