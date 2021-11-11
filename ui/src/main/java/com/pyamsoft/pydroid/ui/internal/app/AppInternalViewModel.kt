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

package com.pyamsoft.pydroid.ui.internal.app

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AppInternalViewModel
internal constructor(
    private val disableDataPolicy: Boolean,
    private val dataPolicyInteractor: DataPolicyInteractor,
) :
    UiViewModel<AppInternalViewState, AppInternalControllerEvent>(
        initialState = AppInternalViewState,
    ) {

  /**
   * Show the Data policy disclosure if it is not disabled
   *
   * If it has been shown or is disabled, show the changelog
   *
   * This does not guarantee that a dialog will be shown, but simply notifies a given dialog that it
   * should be shown if possible
   */
  internal fun handleShowCorrectDialog() {
    viewModelScope.launch(context = Dispatchers.Default) {
      if (disableDataPolicy) {
        publish(AppInternalControllerEvent.ShowChangeLog)
      } else if (!dataPolicyInteractor.isPolicyAccepted()) {
        publish(AppInternalControllerEvent.ShowDataPolicy)
      } else {
        publish(AppInternalControllerEvent.ShowChangeLog)
      }
    }
  }
}
