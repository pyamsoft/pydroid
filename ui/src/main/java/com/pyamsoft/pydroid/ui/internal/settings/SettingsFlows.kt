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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.ui.theme.Theming

@Stable
internal data class SettingsViewState
internal constructor(
    val applicationName: CharSequence,
    val darkMode: Theming.Mode,
    val otherApps: List<OtherApp>,
    val navigationError: Throwable?,
    val isLoading: Boolean,
) : UiViewState

internal sealed class SettingsControllerEvent : UiControllerEvent {

  object NavigateDeveloperPage : SettingsControllerEvent()

  data class NavigateHyperlink internal constructor(val url: String) : SettingsControllerEvent()

  data class OpenOtherAppsScreen internal constructor(val others: List<OtherApp>) :
      SettingsControllerEvent()
}
