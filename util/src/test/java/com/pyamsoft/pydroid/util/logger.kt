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

package com.pyamsoft.pydroid.util

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

public class LoggerTest {

  private class TestLogger : PYDroidLogger {

    var d = 0
    var w = 0
    var e = 0

    override fun d(tag: String, message: () -> String) {
      ++d
    }

    override fun w(tag: String, message: () -> String) {
      ++w
    }

    override fun e(tag: String, throwable: Throwable, message: () -> String) {
      ++e
    }
  }

  @BeforeTest
  public fun onBefore() {
    Logger.resetLogger()
  }

  @Test
  public fun d_doesNothingWithoutImpl(): Unit = runTest {
    Logger.d { throw AssertionError("Does nothing without implementation") }
  }

  @Test
  public fun w_doesNothingWithoutImpl(): Unit = runTest {
    Logger.w { throw AssertionError("Does nothing without implementation") }
  }

  @Test
  public fun e_doesNothingWithoutImpl(): Unit = runTest {
    Logger.e(RuntimeException("Test")) {
      throw AssertionError("Does nothing without implementation")
    }
  }

  @Test
  public fun d_implCalled(): Unit = runTest {
    val t = TestLogger()
    Logger.setLogger(t)
    Logger.d { "Test D" }
    assertEquals(t.d, 1)
    assertEquals(t.w, 0)
    assertEquals(t.e, 0)
  }

  @Test
  public fun w_implCalled(): Unit = runTest {
    val t = TestLogger()
    Logger.setLogger(t)
    Logger.w { "Test W" }
    assertEquals(t.d, 0)
    assertEquals(t.w, 1)
    assertEquals(t.e, 0)
  }

  @Test
  public fun e_implCalled(): Unit = runTest {
    val t = TestLogger()
    Logger.setLogger(t)
    Logger.e(RuntimeException("Test")) { "Test E" }
    assertEquals(t.d, 0)
    assertEquals(t.w, 0)
    assertEquals(t.e, 1)
  }
}
