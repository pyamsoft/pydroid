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
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest

public class EnforcerTest {

  @Test
  public fun noop_doesNothing(): Unit = runTest {
    val noop = createThreadEnforcer(debug = false)
    noop.assertOnMainThread()
    noop.assertOffMainThread()
  }

  @Test
  public fun real_throwsOn(): Unit = runTest {
    val noop = createThreadEnforcer(debug = true)

    try {
      noop.assertOnMainThread()
    } catch (e: Throwable) {
      assertNotNull(e.message)
      assert(e.message!!.startsWith("Method getMainLooper in android.os.Looper not mocked"))
    }
  }

  @Test
  public fun real_throwsOff(): Unit = runTest {
    val noop = createThreadEnforcer(debug = true)

    try {
      noop.assertOffMainThread()
    } catch (e: Throwable) {
      assertNotNull(e.message)
      assert(e.message!!.startsWith("Method getMainLooper in android.os.Looper not mocked"))
    }
  }
}
