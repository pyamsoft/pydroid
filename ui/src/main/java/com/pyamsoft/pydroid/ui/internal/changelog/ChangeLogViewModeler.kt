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

package com.pyamsoft.pydroid.ui.internal.changelog

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.ui.changelog.ChangeLogViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class ChangeLogViewModeler
internal constructor(
    private val state: MutableChangeLogViewState,
    private val interactor: ChangeLogInteractor,
) : AbstractViewModeler<ChangeLogViewState>(state) {

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      // Decide based on preference (have we seen the current version changelog)
      val show = interactor.listenShowChangeLogChanges().first()
      state.canShow = show

      // If we can show, mark as shown so that in the future we do not show.
      if (show) {
        interactor.markChangeLogShown()
      }
    }
  }

  internal fun handleShow(
      scope: CoroutineScope,
      onShowChangeLog: () -> Unit,
  ) {
    scope.launch(context = Dispatchers.Main) {
      interactor.markChangeLogShown()
      onShowChangeLog()
    }
  }

  internal fun handleDismiss() {
    state.canShow = false
  }
}
