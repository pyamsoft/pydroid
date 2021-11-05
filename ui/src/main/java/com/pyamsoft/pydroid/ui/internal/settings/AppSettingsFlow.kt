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

import com.pyamsoft.pydroid.arch.UiControllerEvent
import com.pyamsoft.pydroid.arch.UiViewEvent
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.util.HyperlinkIntent

@Deprecated("Migrate to Jetpack Compose")
internal data class AppSettingsViewState
internal constructor(
    val applicationName: CharSequence,
    val isDarkTheme: DarkTheme?,
    val throwable: Throwable?,
    val otherApps: List<OtherApp>
) : UiViewState {

  data class DarkTheme internal constructor(val dark: Boolean)
}

@Deprecated("Migrate to Jetpack Compose")
internal sealed class AppSettingsViewEvent : UiViewEvent {

  object MoreApps : AppSettingsViewEvent()

  data class Hyperlink internal constructor(val hyperlinkIntent: HyperlinkIntent) :
      AppSettingsViewEvent()

  object RateApp : AppSettingsViewEvent()

  object ViewLicense : AppSettingsViewEvent()

  object CheckUpgrade : AppSettingsViewEvent()

  object ClearData : AppSettingsViewEvent()

  object ShowUpgrade : AppSettingsViewEvent()

  object ShowDonate : AppSettingsViewEvent()

  data class ToggleDarkTheme(val mode: String) : AppSettingsViewEvent()
}

@Deprecated("Migrate to Jetpack Compose")
internal sealed class AppSettingsControllerEvent : UiControllerEvent {

  object NavigateDeveloperPage : AppSettingsControllerEvent()

  data class OpenOtherAppsScreen internal constructor(val others: List<OtherApp>) :
      AppSettingsControllerEvent()

  data class DarkModeChanged internal constructor(val newMode: Theming.Mode) :
      AppSettingsControllerEvent()
}
