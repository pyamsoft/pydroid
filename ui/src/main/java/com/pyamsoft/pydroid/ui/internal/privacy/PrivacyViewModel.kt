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

package com.pyamsoft.pydroid.ui.internal.privacy

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UnitControllerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class PrivacyViewModel internal constructor(
) : UiViewModel<PrivacyViewState, PrivacyViewEvent, UnitControllerEvent>(
    initialState = PrivacyViewState(throwable = null)
) {

    internal inline fun handlePrivacyNavigationEvent(
        scope: CoroutineScope,
        crossinline onEvent: (url: String) -> Unit
    ) {
        scope.launch(context = Dispatchers.Default) {
            PrivacyEventBus.onEvent { onEvent(it.url) }
        }
    }

    internal fun handleHideSnackbar() {
        viewModelScope.setState { copy(throwable = null) }
    }

    fun handleNavigationFailed(error: Throwable) {
        viewModelScope.setState { copy(throwable = error) }
    }

    fun handleNavigationSuccess() {
        viewModelScope.setState { copy(throwable = null) }
    }
}
