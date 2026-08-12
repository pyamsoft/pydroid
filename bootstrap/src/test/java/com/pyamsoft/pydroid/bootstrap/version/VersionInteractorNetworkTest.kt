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

import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.update.AppUpdater
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test

private class FakeAppUpdater(private val action: suspend () -> AppUpdateLauncher) : AppUpdater {
  override suspend fun watchDownloadStatus(
      onDownloadProgress: (Float) -> Unit,
      onDownloadCompleted: () -> Unit,
      onDownloadCancelled: () -> Unit,
      onDownloadFailed: () -> Unit,
  ) = Unit

  override suspend fun checkForUpdate(): AppUpdateLauncher = action()

  override suspend fun completeUpgrade() = Unit
}

public class VersionInteractorNetworkTest {

  @Test
  public fun checkVersion_success_returnsSuccessWrapper(): TestResult = runTest {
    val launcher = AppUpdateLauncher.empty()
    val interactor = VersionInteractorNetwork(FakeAppUpdater { launcher })

    val result = interactor.checkVersion()

    assertNotNull(result.getOrNull())
  }

  @Test
  public fun checkVersion_failure_returnsFailureWrapper(): TestResult = runTest {
    val interactor = VersionInteractorNetwork(FakeAppUpdater { throw RuntimeException("boom") })

    val result = interactor.checkVersion()

    assertNull(result.getOrNull())
    assertNotNull(result.exceptionOrNull())
  }

  @Test
  public fun checkVersion_cancellation_propagatesInsteadOfSwallowing(): TestResult = runTest {
    val interactor =
        VersionInteractorNetwork(FakeAppUpdater { throw CancellationException("cancelled") })

    assertFailsWith<CancellationException> { interactor.checkVersion() }
  }
}
