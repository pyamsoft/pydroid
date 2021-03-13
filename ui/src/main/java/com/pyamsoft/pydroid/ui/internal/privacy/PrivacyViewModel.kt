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
import com.pyamsoft.pydroid.ui.internal.privacy.PrivacyControllerEvent.ViewExternalPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class PrivacyViewModel internal constructor(
) : UiViewModel<PrivacyViewState, PrivacyViewEvent, PrivacyControllerEvent>(
    initialState = PrivacyViewState(throwable = null)
) {

    init {
        viewModelScope.launch(context = Dispatchers.Default) {
            PrivacyEventBus.onEvent { publish(ViewExternalPolicy(it.url)) }
        }
    }

    internal fun handleHideSnackbar() {
        setState { copy(throwable = null) }
    }

    fun navigationFailed(error: Throwable) {
        setState { copy(throwable = error) }
    }

    fun navigationSuccess() {
        setState { copy(throwable = null) }
    }
}
