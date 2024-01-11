/*
 * Copyright 2024 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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

import com.pyamsoft.pydroid.bus.EventBus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

/** Simple implementation of the EventBus interface */
public class DefaultEventBus<T : Any>
@JvmOverloads
public constructor(
    replay: Int = 0,
    extraBufferCapacity: Int = 0,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) : EventBus<T> {

  /** The real bus */
  private val bus =
      MutableSharedFlow<T>(
          replay = replay,
          extraBufferCapacity = extraBufferCapacity,
          onBufferOverflow = onBufferOverflow,
      )

  override val subscriptionCount: StateFlow<Int> = bus.subscriptionCount

  override val replayCache: List<T> = bus.replayCache

  override suspend fun collect(collector: FlowCollector<T>): Nothing = bus.collect(collector)

  override suspend fun emit(value: T): Unit = bus.emit(value)

  @ExperimentalCoroutinesApi override fun resetReplayCache(): Unit = bus.resetReplayCache()

  override fun tryEmit(value: T): Boolean = bus.tryEmit(value)
}
