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

package com.pyamsoft.pydroid.bootstrap.rating

import com.pyamsoft.pydroid.bootstrap.rating.rate.AppRatingLauncher
import com.pyamsoft.pydroid.bootstrap.rating.rate.RateMyApp
import com.pyamsoft.pydroid.core.LintIgnoreTooGenericExceptionThrown
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test

private class FakeRateMyApp(private val action: suspend () -> AppRatingLauncher) : RateMyApp {
  override suspend fun startRating(): AppRatingLauncher = action()
}

public class RatingInteractorImplTest {

  @Test
  public fun askForRating_success_returnsSuccessWrapper(): TestResult = runTest {
    val launcher = AppRatingLauncher.empty()
    val interactor = RatingInteractorImpl(FakeRateMyApp { launcher })

    val result = interactor.askForRating()

    assertNotNull(result.getOrNull())
  }

  @Test
  public fun askForRating_failure_returnsFailureWrapper(): TestResult = runTest {
    val interactor =
        RatingInteractorImpl(
            FakeRateMyApp {
              @LintIgnoreTooGenericExceptionThrown throw RuntimeException("boom")
            }
        )

    val result = interactor.askForRating()

    assertNull(result.getOrNull())
    assertNotNull(result.exceptionOrNull())
  }

  @Test
  public fun askForRating_cancellation_propagatesInsteadOfSwallowing(): TestResult = runTest {
    val interactor =
        RatingInteractorImpl(FakeRateMyApp { throw CancellationException("cancelled") })

    assertFailsWith<CancellationException> { interactor.askForRating() }
  }
}
