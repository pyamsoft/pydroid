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

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ChangeLogViewModeler
internal constructor(
    private val state: MutableChangeLogViewState,
    private val interactor: ChangeLogInteractor,
) : AbstractViewModeler<ChangeLogViewState>(state) {

  @CheckResult
  private suspend fun canShow(): Boolean =
      withContext(context = Dispatchers.IO) {
        val s = state
        val show = s.canShow
        // If this is null, then the bind callback hasn't fired yet so grab it explicitly
        if (show == null) {
          val cs = interactor.listenShowChangeLogChanges().first()
          s.canShow = cs
          return@withContext cs
        } else {
          // Otherwise we just return whatever the latest data is
          return@withContext show
        }
      }

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      interactor.listenShowChangeLogChanges().collectLatest { state.canShow = it }
    }
  }

  internal fun handleShow(
      scope: CoroutineScope,
      force: Boolean,
      onShowChangeLog: () -> Unit,
  ) {
    scope.launch(context = Dispatchers.Main) {
      var show = false
      if (force) {
        show = true
      } else if (canShow()) {
        show = true
      }

      if (show) {
        interactor.markChangeLogShown()
        onShowChangeLog()
      }
    }
  }
}
