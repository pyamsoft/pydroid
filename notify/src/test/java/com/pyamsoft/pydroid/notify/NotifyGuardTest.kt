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

package com.pyamsoft.pydroid.notify

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
public class NotifyGuardTest {

  @Config(sdk = [28])
  @Test
  public fun canPostNotification_belowApi33_isTrue() {
    val guard = NotifyGuard.createDefault(RuntimeEnvironment.getApplication())
    assertTrue(guard.canPostNotification())
  }

  @Config(sdk = [33])
  @Test
  public fun canPostNotification_api33WithoutPermission_isFalse() {
    val guard = NotifyGuard.createDefault(RuntimeEnvironment.getApplication())
    assertFalse(guard.canPostNotification())
  }
}
