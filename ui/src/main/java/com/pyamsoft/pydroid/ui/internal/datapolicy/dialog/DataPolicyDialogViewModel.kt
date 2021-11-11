/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.datapolicy.dialog

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import com.pyamsoft.pydroid.ui.internal.app.AppProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class DataPolicyDialogViewModel
internal constructor(
    private val provider: AppProvider,
    private val interactor: DataPolicyInteractor,
) :
    UiViewModel<DataPolicyDialogViewState, DataPolicyDialogControllerEvent>(
        initialState =
            DataPolicyDialogViewState(
                name = "",
                icon = 0,
                navigationError = null,
            ),
    ) {

  init {
    viewModelScope.launch(context = Dispatchers.Default) {
      val displayName = interactor.getDisplayName()
      setState {
        copy(
            name = displayName,
            icon = provider.applicationIcon,
        )
      }
    }
  }

  internal fun handleAccept() {
    viewModelScope.launch(context = Dispatchers.Default) {
      interactor.acceptPolicy()
      publish(DataPolicyDialogControllerEvent.AcceptPolicy)
    }
  }

  internal fun handleReject() {
    viewModelScope.launch(context = Dispatchers.Default) {
      interactor.acceptPolicy()
      publish(DataPolicyDialogControllerEvent.RejectPolicy)
    }
  }

  internal fun handleNavigationFailed(throwable: Throwable) {
    setState { copy(navigationError = throwable) }
  }

  fun handleNavigationSuccess() {
    handleHideNavigation()
  }

  internal fun handleHideNavigation() {
    setState { copy(navigationError = null) }
  }
}
