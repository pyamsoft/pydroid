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

import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test

public class CollectionTest {

  @Test
  public fun contains_emptyCollection_isFalse() {
    assertFalse(emptyList<Int>().contains { it == 1 })
  }

  @Test
  public fun contains_matchFound_isTrue() {
    assertTrue(listOf(1, 2, 3).contains { it == 2 })
  }

  @Test
  public fun contains_noMatch_isFalse() {
    assertFalse(listOf(1, 2, 3).contains { it == 4 })
  }
}
