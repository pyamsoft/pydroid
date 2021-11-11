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

package com.pyamsoft.pydroid.ui.internal.datapolicy

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import com.pyamsoft.pydroid.core.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class DataPolicyViewModel
internal constructor(
    private val interactor: DataPolicyInteractor,
) :
    UiViewModel<DataPolicyViewState, DataPolicyControllerEvent>(
        initialState = DataPolicyViewState,
    ) {

  internal fun handleShowDisclosure(force: Boolean) {
    viewModelScope.launch(context = Dispatchers.Default) {
      if (force) {
        Logger.d("Force showing DPD")
        publish(DataPolicyControllerEvent.ShowPolicy)
      } else if (!interactor.isPolicyAccepted()) {
        Logger.d("DPD policy not accepted, show")
        publish(DataPolicyControllerEvent.ShowPolicy)
      } else {
        Logger.d("DPD Policy already accepted, no-op")
      }
    }
  }
}
