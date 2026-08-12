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

import kotlin.test.assertNull
import kotlin.test.assertSame
import org.junit.Test

public class CastTest {

  private interface Marker

  private class Impl : Marker

  @Test
  public fun cast_matchingType_returnsSameInstance() {
    val impl = Impl()
    val cast: Impl? = (impl as Any).cast()
    assertSame(impl, cast)
  }

  @Test
  public fun cast_supertype_succeeds() {
    val impl = Impl()
    val cast: Marker? = (impl as Any).cast()
    assertSame(impl, cast)
  }

  @Test
  public fun cast_wrongType_returnsNull() {
    val value: Any = "a string"
    val cast: Impl? = value.cast()
    assertNull(cast)
  }

  @Test
  public fun cast_nullReceiver_returnsNull() {
    val value: Any? = null
    val cast: Impl? = value.cast()
    assertNull(cast)
  }
}
