/*
 * Copyright 2026 pyamsoft
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

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test

public class DefaultEventBusTest {

  @Test
  public fun replayCache_reflectsEmissionsAfterConstruction() {
    val bus = DefaultEventBus<Int>(replay = 1)
    assertEquals(emptyList(), bus.replayCache)

    bus.tryEmit(1)
    assertEquals(listOf(1), bus.replayCache)

    bus.tryEmit(2)
    assertEquals(listOf(2), bus.replayCache)
  }

  @Test
  public fun tryEmit_noCollectorsNoBuffer_succeeds() {
    val bus = DefaultEventBus<Int>()
    assertTrue(bus.tryEmit(1))
  }

  @Test
  public fun tryEmit_bufferFull_dropsWhenConfigured() {
    val bus =
        DefaultEventBus<Int>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    assertTrue(bus.tryEmit(1))
    assertTrue(bus.tryEmit(2))
    assertEquals(emptyList(), bus.replayCache)
  }

  @Test
  public fun replay_deliversToNewSubscriber(): TestResult = runTest {
    val bus = DefaultEventBus<Int>(replay = 2)
    bus.tryEmit(1)
    bus.tryEmit(2)

    val collected = bus.take(2).toList()

    assertEquals(listOf(1, 2), collected)
  }

  @Test
  public fun subscriptionCount_tracksActiveCollectors(): TestResult = runTest {
    val bus = DefaultEventBus<Int>()
    assertEquals(0, bus.subscriptionCount.value)

    val job = launch { bus.collect {} }
    bus.subscriptionCount.first { it == 1 }

    job.cancel()
    job.join()
    assertEquals(0, bus.subscriptionCount.value)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  public fun resetReplayCache_clearsBufferedValues() {
    val bus = DefaultEventBus<Int>(replay = 2)
    bus.tryEmit(1)

    bus.resetReplayCache()

    assertTrue(bus.replayCache.isEmpty())
  }

  @Test
  public fun multipleCollectors_allReceiveEmission(): TestResult = runTest {
    val bus = DefaultEventBus<Int>()

    val jobA = launch { bus.first() }
    val jobB = launch { bus.first() }
    bus.subscriptionCount.first { it == 2 }

    bus.emit(1)

    assertEquals(1, jobA.also { it.join() }.let { 1 })
    jobB.join()
  }
}
