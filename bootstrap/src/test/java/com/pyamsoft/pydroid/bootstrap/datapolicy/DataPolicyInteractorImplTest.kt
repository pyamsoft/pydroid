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

package com.pyamsoft.pydroid.bootstrap.datapolicy

import android.os.Build
import com.pyamsoft.pydroid.util.AppDispatchers
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

private class FakeDataPolicyPreferences : DataPolicyPreferences {
  val accepted = MutableStateFlow(false)
  var respondedWith: Boolean? = null

  override fun listenForPolicyAcceptedChanges() = accepted

  override fun respondToPolicy(accepted: Boolean) {
    respondedWith = accepted
  }
}

@RunWith(RobolectricTestRunner::class)
@Config(
    // Need this here since Robolectric does not yet support API 37 (which is default otherwise)
    minSdk = Build.VERSION_CODES.O,
    maxSdk = Build.VERSION_CODES.BAKLAVA,
)
public class DataPolicyInteractorImplTest {

  private fun newInteractor(preferences: FakeDataPolicyPreferences) =
      DataPolicyInteractorImpl(
          context = RuntimeEnvironment.getApplication(),
          preferences = preferences,
          // TODO(Peter): Do we need test control over dispatchers here?
          dispatchers = AppDispatchers.create(),
      )

  @Test
  public fun acceptPolicy_respondsTrue() {
    val preferences = FakeDataPolicyPreferences()
    val interactor = newInteractor(preferences)

    interactor.acceptPolicy()

    assertEquals(true, preferences.respondedWith)
  }

  @Test
  public fun rejectPolicy_respondsFalse() {
    val preferences = FakeDataPolicyPreferences()
    val interactor = newInteractor(preferences)

    interactor.rejectPolicy()

    assertEquals(false, preferences.respondedWith)
  }

  @Test
  public fun listenForPolicyAcceptedChanges_reflectsPreferences(): TestResult = runTest {
    val preferences = FakeDataPolicyPreferences()
    val interactor = newInteractor(preferences)

    assertEquals(false, interactor.listenForPolicyAcceptedChanges().first())

    preferences.accepted.value = true
    assertEquals(true, interactor.listenForPolicyAcceptedChanges().first())
  }
}
