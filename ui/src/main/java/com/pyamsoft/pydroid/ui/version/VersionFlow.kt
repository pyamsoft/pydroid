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

package com.pyamsoft.pydroid.ui.version

import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher

data class VersionViewState internal constructor(
    val isLoading: Loading?,
    val throwable: Throwable?,
    val isUpdateAvailable: Boolean,
) : UiViewState {

    data class Loading internal constructor(val forced: Boolean)
}

sealed class VersionViewEvent : UiViewEvent {

    object SnackbarHidden : VersionViewEvent()

    object UpdateRestart : VersionViewEvent()
}

sealed class VersionControllerEvent : UiControllerEvent {

    data class ShowUpgrade internal constructor(
        val launcher: AppUpdateLauncher
    ) : VersionControllerEvent()
}
