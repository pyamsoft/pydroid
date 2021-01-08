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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class ChangeLogDialogViewModel internal constructor(
    interactor: ChangeLogInteractor,
    provider: ChangeLogProvider
) : UiViewModel<ChangeLogDialogViewState, ChangeLogDialogViewEvent, ChangeLogDialogControllerEvent>(
    initialState = ChangeLogDialogViewState(
        icon = 0,
        name = "",
        changeLog = emptyList(),
    )
) {

    init {
        viewModelScope.launch(context = Dispatchers.Default) {
            val displayName = interactor.getDisplayName()
            setState {
                copy(
                    name = displayName,
                    icon = provider.applicationIcon,
                    changeLog = provider.changelog.build(),
                )
            }
        }
    }

    override fun handleViewEvent(event: ChangeLogDialogViewEvent) {
        return when (event) {
            is ChangeLogDialogViewEvent.Close -> publish(ChangeLogDialogControllerEvent.Close)
        }
    }
}

