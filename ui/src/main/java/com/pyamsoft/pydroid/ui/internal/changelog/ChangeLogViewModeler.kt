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
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import com.pyamsoft.pydroid.core.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ChangeLogViewModeler
internal constructor(
    private val state: MutableChangeLogViewState,
    private val changeLogInteractor: ChangeLogInteractor,
    private val dataPolicyInteractor: DataPolicyInteractor,
    private val version: Int,
) : AbstractViewModeler<ChangeLogViewState>(state) {

  private var isDpdAccepted: Boolean? = null

  @CheckResult
  private suspend fun canShow(): Boolean =
      withContext(context = Dispatchers.IO) {
        if (version <= 1) {
          Logger.w("Not showing changelog for version <= 1")
          return@withContext false
        }

        val s = state

        val show = s.canShow
        val isAccepted = isDpdAccepted

        // If this is null, then the bind callback hasn't fired yet so grab it explicitly
        val dpd: Boolean
        if (isAccepted == null) {
          // If the DPD is not accepted yet, don't show the Changelog dialog
          dpd = dataPolicyInteractor.listenForPolicyAcceptedChanges().first()
          isDpdAccepted = dpd
        } else {
          dpd = isAccepted
        }

        // If this is null, then the bind callback hasn't fired yet so grab it explicitly
        val result: Boolean
        if (show == null) {
          val cs = changeLogInteractor.listenShowChangeLogChanges().first()
          s.canShow = cs
          result = cs
        } else {
          result = show
        }

        // Do not implicitly show the changelog if the DPD dialog is being shown
        return@withContext result && dpd
      }

  internal fun bind(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Main) {
      changeLogInteractor.listenShowChangeLogChanges().collectLatest { state.canShow = it }
    }

    scope.launch(context = Dispatchers.Main) {
      dataPolicyInteractor.listenForPolicyAcceptedChanges().collectLatest { isDpdAccepted = it }
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
        changeLogInteractor.markChangeLogShown()
        onShowChangeLog()
      }
    }
  }
}
