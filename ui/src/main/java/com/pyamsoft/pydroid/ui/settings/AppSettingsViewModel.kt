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

import android.content.ActivityNotFoundException
import com.pyamsoft.pydroid.arch.impl.BaseUiViewModel
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.AttemptCheckUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.AttemptClearData
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.ChangeDarkTheme
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.NavigateHyperlink
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.NavigateMoreApps
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.NavigateRateApp
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.OpenShowUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsControllerEvent.ShowLicense
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.CheckUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ClearData
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.Hyperlink
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.MoreApps
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.RateApp
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ToggleDarkTheme
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent.ViewLicense
import com.pyamsoft.pydroid.ui.theme.Theming

internal class AppSettingsViewModel internal constructor(
  applicationName: String,
  bugReportUrl: String,
  hideClearAll: Boolean,
  hideUpgradeInformation: Boolean,
  theming: Theming
) : BaseUiViewModel<AppSettingsViewState, AppSettingsViewEvent, AppSettingsControllerEvent>(
    initialState = AppSettingsViewState(
        applicationName = applicationName,
        bugReportUrl = bugReportUrl,
        hideClearAll = hideClearAll,
        hideUpgradeInformation = hideUpgradeInformation,
        isDarkTheme = theming.isDarkTheme(),
        throwable = null
    )
) {

  override fun handleViewEvent(event: AppSettingsViewEvent) {
    return when (event) {
      is MoreApps -> publish(NavigateMoreApps)
      is Hyperlink -> publish(NavigateHyperlink(event.hyperlinkIntent))
      is RateApp -> publish(NavigateRateApp)
      is ViewLicense -> publish(ShowLicense)
      is CheckUpgrade -> publish(AttemptCheckUpgrade)
      is ClearData -> publish(AttemptClearData)
      is ShowUpgrade -> publish(OpenShowUpgrade)
      is ToggleDarkTheme -> publish(ChangeDarkTheme(event.isDark))
    }
  }

  fun navigationFailed(error: ActivityNotFoundException) {
    setState { copy(throwable = error) }
  }

}
