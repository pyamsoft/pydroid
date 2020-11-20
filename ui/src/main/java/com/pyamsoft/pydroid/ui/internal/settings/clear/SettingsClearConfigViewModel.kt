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

package com.pyamsoft.pydroid.ui.internal.settings.clear

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiStateViewModel
import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.bootstrap.settings.SettingsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class SettingsClearConfigViewModel internal constructor(
    private val interactor: SettingsInteractor,
) : UiStateViewModel<UnitViewState>(initialState = UnitViewState) {

    internal fun reset() {
        viewModelScope.launch(context = Dispatchers.Default) {
            interactor.clear()
        }
    }
}
