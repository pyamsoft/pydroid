/*
 * Copyright 2020 Peter Kenji Yamanaka
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class ChangeLogViewModeler
internal constructor(
    state: MutableChangeLogViewState,
    private val interactor: ChangeLogInteractor
) : AbstractViewModeler<ChangeLogViewState>(state) {

  internal fun handleShow(
      scope: CoroutineScope,
      force: Boolean,
      onShowChangeLog: () -> Unit,
  ) {
    scope.launch(context = Dispatchers.Main) {
      var show = false
      if (force) {
        show = true
      } else if (interactor.canShowChangeLog()) {
        show = true
      }

      if (show) {
        interactor.markChangeLogShown()
        onShowChangeLog()
      }
    }
  }
}