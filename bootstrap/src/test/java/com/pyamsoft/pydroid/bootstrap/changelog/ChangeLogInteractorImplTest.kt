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

package com.pyamsoft.pydroid.bootstrap.changelog

import android.os.Build
import com.pyamsoft.pydroid.util.AppDispatchers
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

private class FakeChangeLogPreferences : ChangeLogPreferences {
  val showChangelog = MutableStateFlow(false)
  var markShownCalled = false

  override fun listenForShowChangelogChanges() = showChangelog

  override fun markChangeLogShown() {
    markShownCalled = true
  }
}

@RunWith(RobolectricTestRunner::class)
@Config(
    // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
    minSdk = Build.VERSION_CODES.O,
    maxSdk = Build.VERSION_CODES.BAKLAVA,
)
public class ChangeLogInteractorImplTest {

  @Test
  public fun listenShowChangeLogChanges_noFake_reflectsPreferences(): TestResult = runTest {
    val preferences = FakeChangeLogPreferences()
    val interactor =
        ChangeLogInteractorImpl(
            context = RuntimeEnvironment.getApplication(),
            preferences = preferences,
            // TODO(Peter): Do we need test control over dispatchers here?
            dispatchers = AppDispatchers.create(),
            isFakeChangeLogAvailable = null,
        )

    assertFalse(interactor.listenShowChangeLogChanges().first())

    preferences.showChangelog.value = true
    assertTrue(interactor.listenShowChangeLogChanges().first())
  }

  @Test
  public fun listenShowChangeLogChanges_fakedTrue_overridesPreferences(): TestResult = runTest {
    val preferences = FakeChangeLogPreferences()
    val interactor =
        ChangeLogInteractorImpl(
            context = RuntimeEnvironment.getApplication(),
            preferences = preferences,
            // TODO(Peter): Do we need test control over dispatchers here?
            dispatchers = AppDispatchers.create(),
            isFakeChangeLogAvailable = MutableStateFlow(true),
        )

    assertTrue(interactor.listenShowChangeLogChanges().first())
  }

  @Test
  public fun markChangeLogShown_delegatesToPreferences() {
    val preferences = FakeChangeLogPreferences()
    val interactor =
        ChangeLogInteractorImpl(
            context = RuntimeEnvironment.getApplication(),
            preferences = preferences,
            // TODO(Peter): Do we need test control over dispatchers here?
            dispatchers = AppDispatchers.create(),
            isFakeChangeLogAvailable = null,
        )

    interactor.markChangeLogShown()

    assertTrue(preferences.markShownCalled)
  }
}
