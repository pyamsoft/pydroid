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

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.version.fake.FakeUpgradeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

internal class DebugViewModeler
internal constructor(
    override val state: MutableDebugViewState,
    private val interactor: DebugInteractor,
    private val preferences: DebugPreferences,
) : DebugViewState by state, AbstractViewModeler<DebugViewState>(state) {

  internal fun bind(scope: CoroutineScope) {
    val s = state

    preferences.listenForInAppDebuggingEnabled().also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { s.isInAppDebuggingEnabled.value = it }
      }
    }

    preferences.listenUpgradeScenarioAvailable().also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { s.debugFakeVersionUpdate.value = it }
      }
    }

    preferences.listenTryShowRatingUpsell().also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { s.isDebugFakeShowRatingUpsell.value = it }
      }
    }

    preferences.listenShowBillingUpsell().also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { s.isDebugFakeShowBillingUpsell.value = it }
      }
    }

    preferences.listenShowChangelogUpsell().also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { s.isDebugFakeShowChangelog.value = it }
      }
    }
  }

  internal fun handleCopy(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Default) {
      val lines = state.inAppDebuggingLogLines.value
      interactor.copyInAppDebugMessagesToClipboard(lines)
    }
  }

  internal fun handleToggleShowRatingUpsell() {
    val newState = state.isDebugFakeShowRatingUpsell.updateAndGet { !it }
    preferences.setTryShowRatingUpsell(newState)
  }

  internal fun handleToggleShowBillingUpsell() {
    val newState = state.isDebugFakeShowBillingUpsell.updateAndGet { !it }
    preferences.setShowBillingUpsell(newState)
  }

  internal fun handleToggleShowChangelog() {
    val newState = state.isDebugFakeShowChangelog.updateAndGet { !it }
    preferences.setShowChangelogUpsell(newState)
  }

  internal fun handleUpdateVersionRequest(request: FakeUpgradeRequest?) {
    val newState = state.debugFakeVersionUpdate.updateAndGet { request }
    preferences.setUpgradeScenarioAvailable(newState)
  }
}
