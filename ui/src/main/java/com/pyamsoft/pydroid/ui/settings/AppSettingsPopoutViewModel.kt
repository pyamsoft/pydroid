/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui.settings

import com.pyamsoft.pydroid.arch.UiViewModel
import timber.log.Timber

internal class AppSettingsPopoutViewModel internal constructor(
    initialName: String,
    debug: Boolean
) : UiViewModel<AppSettingsPopoutViewState, AppSettingsPopoutViewEvent, AppSettingsPopoutControllerEvent>(
    initialState = AppSettingsPopoutViewState(
        name = initialName
    ),
    debug = debug
) {

    override fun handleViewEvent(event: AppSettingsPopoutViewEvent) {
        Timber.d("View event: $event")
        return when (event) {
            is AppSettingsPopoutViewEvent.ClosePopout -> publish(AppSettingsPopoutControllerEvent.ClosePopout)
        }
    }
}
