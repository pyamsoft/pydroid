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

package com.pyamsoft.pydroid.ui.internal.about

import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary

internal data class AboutViewState internal constructor(
    val isLoading: Boolean,
    val licenses: List<OssLibrary>,
    val loadError: Throwable?,
    val navigationError: Throwable?
) : UiViewState

internal sealed class AboutViewEvent : UiViewEvent {

    data class OpenLibrary internal constructor(val index: Int) : AboutViewEvent()

    data class OpenLicense internal constructor(val index: Int) : AboutViewEvent()

    object HideNavigationError : AboutViewEvent()

    object HideLoadError : AboutViewEvent()
}

internal sealed class AboutControllerEvent : UiControllerEvent {

    data class ExternalUrl internal constructor(val url: String) : AboutControllerEvent()
}
