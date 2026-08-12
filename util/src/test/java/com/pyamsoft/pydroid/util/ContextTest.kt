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

import android.content.pm.ApplicationInfo
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 26)
public class ContextTest {

  @Test
  public fun isDebugMode_flagSet_isTrue() {
    val context = RuntimeEnvironment.getApplication()
    context.applicationInfo.flags = context.applicationInfo.flags or ApplicationInfo.FLAG_DEBUGGABLE
    assertTrue(context.isDebugMode())
  }

  @Test
  public fun isDebugMode_flagUnset_isFalse() {
    val context = RuntimeEnvironment.getApplication()
    context.applicationInfo.flags =
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE.inv()
    assertFalse(context.isDebugMode())
  }

  @Test
  public fun applicationDisplayName_cachesAfterFirstResolve() {
    val context = RuntimeEnvironment.getApplication()

    context.applicationInfo.nonLocalizedLabel = "First Label"
    assertEquals("First Label", context.applicationDisplayName)

    context.applicationInfo.nonLocalizedLabel = "Second Label"
    assertEquals("First Label", context.applicationDisplayName)
  }
}
