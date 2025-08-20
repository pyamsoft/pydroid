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

package com.pyamsoft.pydroid.ui.internal.billing

import androidx.compose.runtime.saveable.SaveableStateRegistry
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.core.cast
import com.pyamsoft.pydroid.ui.billing.BillingViewState
import com.pyamsoft.pydroid.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal class BillingViewModeler
internal constructor(
    override val state: MutableBillingViewState,
    private val preferences: BillingPreferences,
    private val isFakeUpsell: Flow<Boolean>?,
) : BillingViewState by state, AbstractViewModeler<BillingViewState>(state) {

  override fun registerSaveState(
      registry: SaveableStateRegistry
  ): List<SaveableStateRegistry.Entry> =
      mutableListOf<SaveableStateRegistry.Entry>().apply {
        val s = state

        registry.registerProvider(KEY_SHOW_DIALOG) { s.isShowingDialog.value }.also { add(it) }
      }

  override fun consumeRestoredState(registry: SaveableStateRegistry) {
    val s = state

    registry.consumeRestored(KEY_SHOW_DIALOG)?.cast<Boolean>()?.also {
      s.isShowingDialog.value = it
    }
  }

  internal fun bind(scope: CoroutineScope) {
    val s = state

    preferences.listenForBillingUpsellChanges().also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { show ->
          if (show) {
            Logger.d { "Showing Billing upsell" }
            s.isShowingUpsell.value = true
          }
        }
      }
    }

    isFakeUpsell?.also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { show ->
          if (show) {
            Logger.d { "Fake a billing upsell, force show" }
            s.isShowingUpsell.value = true
          }
        }
      }
    }
  }

  internal fun handleOpenDialog() {
    state.isShowingDialog.value = true
  }

  internal fun handleCloseDialog() {
    state.isShowingDialog.value = false
  }

  internal fun handleDismissUpsell() {
    Logger.d { "Dismissing Billing upsell" }
    state.isShowingUpsell.value = false
    preferences.resetBillingShown()
  }

  internal fun handleMaybeShowUpsell() {
    preferences.maybeShowBillingUpsell()
  }

  companion object {
    private const val KEY_SHOW_DIALOG = "billing_show_dialog"
  }
}
