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

package com.pyamsoft.pydroid.ui.version.upgrade

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class VersionUpgradeViewModel internal constructor(
    private val interactor: VersionInteractor,
    debug: Boolean
) : UiViewModel<UnitViewState, UnitViewEvent, VersionUpgradeControllerEvent>(
    initialState = UnitViewState, debug = debug
) {

    override fun handleViewEvent(event: UnitViewEvent) {
    }

    internal fun completeUpgrade() {
        viewModelScope.launch(context = Dispatchers.Default) {
            Timber.d("Updating app, restart via update manager!")
            interactor.completeUpdate()

            withContext(context = Dispatchers.Main) {
                Timber.d("App update completed, publish finish!")
                publish(VersionUpgradeControllerEvent.FinishedUpgrade)
            }
        }
    }
}
