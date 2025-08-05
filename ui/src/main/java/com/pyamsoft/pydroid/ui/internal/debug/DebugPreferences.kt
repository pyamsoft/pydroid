/*
 * Copyright 2025 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.debug

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.version.fake.FakeUpgradeRequest
import com.pyamsoft.pydroid.ui.debug.InAppDebugStatus
import kotlinx.coroutines.flow.Flow

/** In-App Debugging preferences */
internal interface DebugPreferences : InAppDebugStatus {

  /** Mark the debugging on or off */
  fun setInAppDebuggingEnabled(enabled: Boolean)

  /** Fake an in-app upgrade availability */
  fun setUpgradeScenarioAvailable(fake: FakeUpgradeRequest?)

  /** Watch for changes to fake in-app upgrade */
  @CheckResult fun listenUpgradeScenarioAvailable(): Flow<FakeUpgradeRequest>

  /** Fake show the changelog tooltip */
  fun setShowChangelogUpsell(show: Boolean)

  /** Watch for changes to fake changelog */
  @CheckResult fun listenShowChangelogUpsell(): Flow<Boolean>

  /** Fake show the billing upsell */
  fun setShowBillingUpsell(show: Boolean)

  /** Watch for changes to fake billing upsell */
  @CheckResult fun listenShowBillingUpsell(): Flow<Boolean>

  /** Fake show the in-app rating dialog (not guaranteed, thanks Google) */
  fun setTryShowRatingUpsell(show: Boolean)

  /** Watch for changes to fake rating upsell */
  @CheckResult fun listenTryShowRatingUpsell(): Flow<Boolean>
}
