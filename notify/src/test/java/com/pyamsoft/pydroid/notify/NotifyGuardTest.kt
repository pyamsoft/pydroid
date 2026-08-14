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

import android.os.Build
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
public class NotifyGuardTest {

  @Config(
      // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
      minSdk = Build.VERSION_CODES.O,
      maxSdk = Build.VERSION_CODES.S_V2,
  )
  @Test
  public fun canPostNotification_belowApi33_isTrue() {
    val guard = NotifyGuard.createDefault(RuntimeEnvironment.getApplication())
    assertTrue(guard.canPostNotification())
  }

  @Config(
      // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
      minSdk = Build.VERSION_CODES.TIRAMISU,
      maxSdk = Build.VERSION_CODES.BAKLAVA,
  )
  @Test
  public fun canPostNotification_api33WithoutPermission_isFalse() {
    val guard = NotifyGuard.createDefault(RuntimeEnvironment.getApplication())
    assertFalse(guard.canPostNotification())
  }
}
