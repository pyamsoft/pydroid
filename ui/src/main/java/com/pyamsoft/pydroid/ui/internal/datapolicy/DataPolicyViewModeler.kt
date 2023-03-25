/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.pydroid.ui.internal.datapolicy

import androidx.compose.runtime.saveable.SaveableStateRegistry
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class DataPolicyViewModeler
internal constructor(
    override val state: MutableDataPolicyViewState,
    private val interactor: DataPolicyInteractor,
) : AbstractViewModeler<DataPolicyViewState>(state) {

  override fun registerSaveState(
      registry: SaveableStateRegistry
  ): List<SaveableStateRegistry.Entry> =
      mutableListOf<SaveableStateRegistry.Entry>().apply {
        registry.registerProvider(KEY_SHOW_DIALOG) { state.isAccepted.value.name }.also { add(it) }
      }

  override fun consumeRestoredState(registry: SaveableStateRegistry) {
    registry
        .consumeRestored(KEY_SHOW_DIALOG)
        ?.let { it as String }
        ?.let { DataPolicyViewState.AcceptedState.valueOf(it) }
        ?.also { state.isAccepted.value = it }
  }

  internal fun bind(
      scope: CoroutineScope,
  ) {
    scope.launch(context = Dispatchers.Main) {
      interactor.listenForPolicyAcceptedChanges().collectLatest { accepted ->
        state.isAccepted.value =
            if (accepted) DataPolicyViewState.AcceptedState.ACCEPTED
            else DataPolicyViewState.AcceptedState.REJECTED
      }
    }
  }

  companion object {

    private const val KEY_SHOW_DIALOG = "datapolicy_show_dialog"
  }
}
