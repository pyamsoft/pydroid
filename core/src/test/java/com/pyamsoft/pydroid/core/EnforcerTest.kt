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

package com.pyamsoft.pydroid.core

import kotlin.test.assertFails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 26)
// Need to set the LooperMode or tests using Dispatcher.Main block forever
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
public class EnforcerTest {

  /** In a production application, the enforcer should be a no-op */
  @Test
  public fun noop_doesNothing() {
    val noop = createThreadEnforcer(debug = false)
    noop.assertOnMainThread()
    noop.assertOffMainThread()
  }

  /** During debugging/development, the enforcer should ACTUALLY throw though */
  @Test
  public fun real_enforcesThreadingModel(): Unit = runTest {
    val enforcer = createThreadEnforcer(debug = true)
    withContext(Dispatchers.Main) {
      // Should not throw
      enforcer.assertOnMainThread()

      // Should throw
      assertFails { enforcer.assertOffMainThread() }
    }

    withContext(Dispatchers.Default) {
      // No throw
      enforcer.assertOffMainThread()

      // Throw
      assertFails { enforcer.assertOnMainThread() }
    }
  }
}
