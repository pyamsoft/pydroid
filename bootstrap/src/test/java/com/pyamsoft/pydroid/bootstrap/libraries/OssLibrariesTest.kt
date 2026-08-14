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

package com.pyamsoft.pydroid.bootstrap.libraries

import android.os.Build
import com.pyamsoft.pydroid.bootstrap.R
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
    // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
    minSdk = Build.VERSION_CODES.O,
    maxSdk = Build.VERSION_CODES.BAKLAVA,
)
public class OssLibrariesTest {

  @Before
  public fun setup() {
    OssLibraries.resetForTests()
  }

  @Test
  public fun libraries_alwaysIncludesBootstrapLibraries() {
    val context = RuntimeEnvironment.getApplication()

    val result = OssLibraries.libraries(context)

    assertTrue(result.isNotEmpty())
  }

  @Test
  public fun libraries_usingArchFalse_doesNotIncludeArchLibrary() {
    val context = RuntimeEnvironment.getApplication()
    OssLibraries.usingArch = false

    val result = OssLibraries.libraries(context)

    assertTrue(result.none { it.name == context.getString(R.string.pydroid_arch) })
  }

  @Test
  public fun libraries_usingArchTrue_includesArchLibrary() {
    val context = RuntimeEnvironment.getApplication()
    OssLibraries.usingArch = true

    val result = OssLibraries.libraries(context)

    assertTrue(result.any { it.name == context.getString(R.string.pydroid_arch) })
  }

  @Test
  public fun add_duplicateKey_isNotAddedTwice() {
    val before = OssLibraries.libraries(RuntimeEnvironment.getApplication()).size

    OssLibraries.add("Some Library", "https://example.com", "A description")
    val afterFirst = OssLibraries.libraries(RuntimeEnvironment.getApplication()).size
    OssLibraries.add("Some Library", "https://example.com", "A description")
    val afterSecond = OssLibraries.libraries(RuntimeEnvironment.getApplication()).size

    assertEquals(before + 1, afterFirst)
    assertEquals(afterFirst, afterSecond)
  }
}
