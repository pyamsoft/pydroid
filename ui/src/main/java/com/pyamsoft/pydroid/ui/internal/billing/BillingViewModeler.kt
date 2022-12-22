/*
 * Copyright 2022 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.internal.billing

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.core.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class BillingViewModeler
internal constructor(
    private val preferences: BillingPreferences,
    private val state: MutableBillingViewState,
) : AbstractViewModeler<BillingViewState>(state) {

  internal fun bind(scope: CoroutineScope) {
    val s = state
    scope.launch(context = Dispatchers.Main) {
      preferences.listenForUpsellChanges().collectLatest { show ->
        if (show) {
          Logger.d("Showing Billing upsell")
          s.showUpsell = true
        }
      }
    }
  }

  internal fun handleDismissUpsell() {
    Logger.d("Temporary dismissing Billing upsell")
    state.showUpsell = false
  }

  internal fun handleMaybeShowUpsell(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) { preferences.maybeShowUpsell() }
  }
}
