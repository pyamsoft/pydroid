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

package com.pyamsoft.pydroid.ui.internal.changelog

import androidx.compose.runtime.saveable.SaveableStateRegistry
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.core.cast
import com.pyamsoft.pydroid.ui.changelog.ChangeLogViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class ChangeLogViewModeler
internal constructor(
    override val state: MutableChangeLogViewState,
    private val interactor: ChangeLogInteractor,
) : ChangeLogViewState by state, AbstractViewModeler<ChangeLogViewState>(state) {

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
    interactor.listenShowChangeLogChanges().also { f ->
      scope.launch(context = Dispatchers.Default) { f.collect { state.isShowUpsell.value = it } }
    }
  }

  internal fun handleShowDialog() {
    state.isShowingDialog.value = true
  }

  internal fun handleCloseDialog() {
    state.isShowingDialog.value = false
  }

  internal fun handleDismissUpsell() {
    state.isShowUpsell.value = false

    // mark as shown so that in the future we do not show.
    interactor.markChangeLogShown()
  }

  companion object {
    private const val KEY_SHOW_DIALOG = "changelog_show_dialog"
  }
}
