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

package com.pyamsoft.pydroid.ui.otherapps

import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp

data class OtherAppsViewState(
    val apps: List<OtherApp>,
    val navigationError: Throwable?
) : UiViewState

sealed class OtherAppsViewEvent : UiViewEvent {

    data class OpenStore internal constructor(val index: Int) : OtherAppsViewEvent()

    data class ViewSource internal constructor(val index: Int) : OtherAppsViewEvent()
}

sealed class OtherAppsControllerEvent : UiControllerEvent {

    data class ExternalUrl internal constructor(val url: String) : OtherAppsControllerEvent()
}
