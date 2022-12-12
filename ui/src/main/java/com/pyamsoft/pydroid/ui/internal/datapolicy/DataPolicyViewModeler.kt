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

package com.pyamsoft.pydroid.ui.internal.datapolicy

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class DataPolicyViewModeler
internal constructor(
    private val state: MutableDataPolicyViewState,
    private val interactor: DataPolicyInteractor,
) : AbstractViewModeler<DataPolicyViewState>(state) {

  internal fun bind(
      scope: CoroutineScope,
      onShowPolicy: () -> Unit,
  ) {
    scope.launch(context = Dispatchers.Main) {
      interactor.listenForPolicyAcceptedChanges().collectLatest { accepted ->
        state.isAccepted = accepted
        if (!accepted) {
          onShowPolicy()
        }
      }
    }
  }

  /** Used from DPD Delegate */
  internal fun handleShowDataPolicyDialogIfPossible(
      scope: CoroutineScope,
      onNeedsToShow: () -> Unit,
  ) {
    scope.launch(context = Dispatchers.Main) {
      if (state.isAccepted == false) {
        onNeedsToShow()
      }
    }
  }
}
