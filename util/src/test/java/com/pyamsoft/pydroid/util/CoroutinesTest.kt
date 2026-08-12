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

package com.pyamsoft.pydroid.util

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Test

public class CoroutinesTest {

  @Test
  public fun ifNotCancellation_cancellation_rethrows() {
    val cancellation = CancellationException("cancelled")
    val thrown = assertFailsWith<CancellationException> { cancellation.ifNotCancellation { 1 } }
    assertSame(cancellation, thrown)
  }

  @Test
  public fun ifNotCancellation_cancellationSubtype_rethrows(): TestResult = runTest {
    val timeout =
        try {
          withTimeout(1.milliseconds) { delay(1.seconds) }
          error("expected timeout")
        } catch (e: TimeoutCancellationException) {
          e
        }

    val thrown = assertFailsWith<CancellationException> { timeout.ifNotCancellation { 1 } }
    assertIs<TimeoutCancellationException>(thrown)
    assertSame(timeout, thrown)
  }

  @Test
  public fun ifNotCancellation_otherThrowable_runsBlock() {
    val result = RuntimeException("boom").ifNotCancellation { 42 }
    assertEquals(42, result)
  }

  @Test
  public fun ifNotCancellation_blockThrows_propagates() {
    val error = IllegalStateException("nope")
    val thrown =
        assertFailsWith<IllegalStateException> {
          RuntimeException("boom").ifNotCancellation { throw error }
        }
    assertSame(error, thrown)
  }
}
