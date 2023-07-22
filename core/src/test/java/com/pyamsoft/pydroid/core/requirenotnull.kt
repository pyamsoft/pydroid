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

package com.pyamsoft.pydroid.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot
import kotlin.test.assertSame
import kotlinx.coroutines.test.runTest

public class RequireNotNullTest {

  @Test
  public fun rnn_noopNonNull(): Unit = runTest {
    val i = emptyList<Nothing>()
    val nn = i.requireNotNull()
    assertEquals(i, nn)
    assertSame(i, nn)
  }

  @Test
  public fun rnn_nullThrows(): Unit = runTest {
    val i: String? = null
    try {
      i.requireNotNull()
      throw AssertionError("Expected to throw")
    } catch (e: Throwable) {
      assertIsNot<AssertionError>(e)
      assertIs<IllegalArgumentException>(e)
    }
  }

  @Test
  public fun rnn_nullThrowsWithMessage(): Unit = runTest {
    val i: String? = null
    try {
      i.requireNotNull { "MY MESSAGE" }
      throw AssertionError("Expected to throw")
    } catch (e: Throwable) {
      assertIsNot<AssertionError>(e)
      assertIs<IllegalArgumentException>(e)
      assertEquals(e.message, "MY MESSAGE")
    }
  }
}
