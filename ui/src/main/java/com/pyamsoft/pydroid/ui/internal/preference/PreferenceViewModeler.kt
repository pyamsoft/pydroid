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

package com.pyamsoft.pydroid.ui.internal.preference

import androidx.compose.runtime.saveable.SaveableStateRegistry
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.core.cast
import kotlinx.coroutines.flow.update

internal class PreferenceViewModeler
internal constructor(
    override val state: MutablePreferenceViewState,
) : PreferenceViewState by state, AbstractViewModeler<PreferenceViewState>(state) {

  override fun registerSaveState(
      registry: SaveableStateRegistry
  ): List<SaveableStateRegistry.Entry> =
      mutableListOf<SaveableStateRegistry.Entry>().apply {
        registry
            .registerProvider(KEY_DIALOGS) {
              // Add each ID with a space between them
              var ids = ""
              for (entry in state.dialogStates.value) {
                if (entry.value) {
                  ids = "$ids ${entry.key}".trim()
                }
              }

              return@registerProvider ids.trim()
            }
            .also { add(it) }
      }

  override fun consumeRestoredState(registry: SaveableStateRegistry) {
    registry
        .consumeRestored(KEY_DIALOGS)
        ?.cast<String>()
        // IDs are space split
        ?.split(" ")
        ?.also { ids ->
          // Add the ids back into the map
          state.dialogStates.update { map ->
            var r = map
            for (id in ids) {
              r = r + (id to true)
            }

            return@update r
          }
        }
  }

  fun handleShowDialog(preferenceId: String) {
    state.dialogStates.update { it + (preferenceId.trim() to true) }
  }

  fun handleDismissDialog(preferenceId: String) {
    state.dialogStates.update { it + (preferenceId.trim() to false) }
  }

  companion object {
    private const val KEY_DIALOGS = "preferences_showing_dialogs"
  }
}
