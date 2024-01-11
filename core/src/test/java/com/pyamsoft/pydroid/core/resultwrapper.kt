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

package com.pyamsoft.pydroid.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

public class ResultWrapperTest {

  @Test
  public fun do_not_do_this(): Unit = runTest {
    try {
      val r = ResultWrapper(data = null, error = null).map { 1 }
      assertEquals(r.getOrThrow(), 1)
      throw AssertionError("Bad Constructor")
    } catch (e: Throwable) {
      assertIsNot<AssertionError>(e)
      assertIs<IllegalStateException>(e)
    }
  }

  @Test
  public fun create_success(): Unit = runTest {
    val r = ResultWrapper.success(0)
    assertEquals(r.getOrThrow(), 0)
    assertNull(r.exceptionOrNull())
  }

  @Test
  public fun create_failure(): Unit = runTest {
    val t = RuntimeException("TEST")
    val r = ResultWrapper.failure<Nothing>(t)
    assertEquals(r.exceptionOrThrow(), t)
    assertNull(r.getOrNull())
  }

  @Test
  public fun map_successOnly(): Unit = runTest {
    val r = ResultWrapper.success(0).map { 1 }
    assertEquals(r.getOrThrow(), 1)
    assertNull(r.exceptionOrNull())
  }

  @Test
  public fun map_failureNoOp(): Unit = runTest {
    val t = RuntimeException("TEST")
    val r = ResultWrapper.failure<Int>(t).map { 1 }
    assertEquals(r.exceptionOrThrow(), t)
    assertNull(r.getOrNull())
  }

  @Test
  public fun map_recoverSuccess(): Unit = runTest {
    val t = RuntimeException("TEST")
    val r = ResultWrapper.failure<Int>(t).recover { 1 }
    assertEquals(r.getOrThrow(), 1)
    assertNull(r.exceptionOrNull())
  }

  @Test
  public fun map_recoverMapFailureToSuccess(): Unit = runTest {
    val r = ResultWrapper.success(0).map<Int> { throw RuntimeException("TEST") }.recover { 1 }
    assertEquals(r.getOrThrow(), 1)
    assertNull(r.exceptionOrNull())
  }

  @Test
  public fun recover_throwsOnSuccess(): Unit = runTest {
    val t = RuntimeException("Test")
    val r = ResultWrapper.success(0).map { 1 }.onSuccess { throw t }

    assertEquals(r.exceptionOrThrow(), t)
    assertNull(r.getOrNull())
  }

  @Test
  public fun recover_recoverOnSuccess(): Unit = runTest {
    val r =
        ResultWrapper.success(0)
            .map { 1 }
            .onSuccess { throw RuntimeException("Test") }
            .recover { 2 }

    assertEquals(r.getOrThrow(), 2)
    assertNull(r.exceptionOrNull())
  }

  @Test
  public fun recover_throwsOnError(): Unit = runTest {
    val tt = RuntimeException("Test2")
    val r =
        ResultWrapper.success(0)
            .map { 1 }
            .onSuccess { throw RuntimeException("Test") }
            .recover { throw tt }

    assertEquals(r.exceptionOrThrow(), tt)
    assertNull(r.getOrNull())
  }

  @Test
  public fun recover_recoverOnRecover(): Unit = runTest {
    val tt = RuntimeException("Test2")
    val r =
        ResultWrapper.success(0)
            .map { 1 }
            .onSuccess { throw RuntimeException("Test") }
            .recover { throw tt }
            .recover { 3 }

    assertEquals(r.getOrThrow(), 3)
    assertNull(r.exceptionOrNull())
  }

  @Test
  public fun map_onSuccess(): Unit = runTest {
    var c = 0
    var d = 0
    var e = 0
    val r =
        ResultWrapper.success(0)
            .map { 1 }
            .onSuccess { c = it }
            .map { 2 }
            .onSuccess { d = it }
            .onFailure { e = 1 }

    assertEquals(r.getOrThrow(), 2)
    assertNull(r.exceptionOrNull())
    assertEquals(c, 1)
    assertEquals(d, 2)
    assertEquals(e, 0)
  }

  @Test
  public fun map_onFailure(): Unit = runTest {
    var c = 0
    var s = 0
    val t = RuntimeException("TEST")
    val r = ResultWrapper.failure<Nothing>(t).onSuccess { s = 1 }.onFailure { c = 1 }

    assertEquals(r.exceptionOrThrow(), t)
    assertNull(r.getOrNull())
    assertEquals(c, 1)
    assertEquals(s, 0)
  }

  @Test
  public fun map_onFinally(): Unit = runTest {
    var r1s = 0
    var r1e = 0
    var r1f = 0

    var r2s = 0
    var r2e = 0
    var r2f = 0

    val t = RuntimeException("TEST")
    val r1 =
        ResultWrapper.failure<Nothing>(t)
            .onSuccess { r1s = 1 }
            .onFailure { r1e = 1 }
            .onFinally { r1f = 1 }

    assertEquals(r1.exceptionOrThrow(), t)
    assertNull(r1.getOrNull())
    assertEquals(r1f, 1)
    assertEquals(r1e, 1)
    assertEquals(r1s, 0)

    val r2 =
        ResultWrapper.success(1).onSuccess { r2s = 1 }.onFailure { r2e = 1 }.onFinally { r2f = 1 }

    assertEquals(r2.getOrThrow(), 1)
    assertNull(r2.exceptionOrNull())
    assertEquals(r2f, 1)
    assertEquals(r2s, 1)
    assertEquals(r2e, 0)
  }
}
