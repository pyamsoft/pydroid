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

package com.pyamsoft.pydroid.bootstrap.version

import kotlin.test.assertEquals
import org.junit.Test

public class AbstractAppUpdaterTest {

  @Test
  public fun calculateDownloadProgress_totalBytesZero_isZeroNotNaN() {
    val progress = AbstractAppUpdater.calculateDownloadProgress(0L, 0L)
    assertEquals(0F, progress)
  }

  @Test
  public fun calculateDownloadProgress_totalBytesNegative_isZero() {
    val progress = AbstractAppUpdater.calculateDownloadProgress(0L, -1L)
    assertEquals(0F, progress)
  }

  @Test
  public fun calculateDownloadProgress_halfDownloaded_isOneHalf() {
    val progress = AbstractAppUpdater.calculateDownloadProgress(50L, 100L)
    assertEquals(0.5F, progress)
  }

  @Test
  public fun calculateDownloadProgress_fullyDownloaded_isOne() {
    val progress = AbstractAppUpdater.calculateDownloadProgress(100L, 100L)
    assertEquals(1F, progress)
  }
}
