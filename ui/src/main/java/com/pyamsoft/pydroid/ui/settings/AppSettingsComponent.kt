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

import androidx.annotation.CheckResult
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.NavigationViewModel
import com.pyamsoft.pydroid.ui.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.rating.ShowRating
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckState
import com.pyamsoft.pydroid.ui.version.VersionCheckViewModel

internal interface AppSettingsComponent {

  fun inject(fragment: AppSettingsPreferenceFragment)

  interface Factory {

    @CheckResult
    fun create(
      preferenceScreen: PreferenceScreen,
      hideClearAll: Boolean,
      hideUpgradeInformation: Boolean
    ): AppSettingsComponent

  }

  class Impl private constructor(
    private val applicationName: String,
    private val bugReportUrl: String,
    private val hideClearAll: Boolean,
    private val hideUpgradeInformation: Boolean,
    private val preferenceScreen: PreferenceScreen,
    private val theming: Theming,
    private val schedulerProvider: SchedulerProvider,
    private val settingsBus: EventBus<AppSettingsEvent>,
    private val versionCheckBus: EventBus<VersionCheckState>,
    private val ratingBus: EventBus<ShowRating>,
    private val navigationBus: EventBus<FailedNavigationEvent>,
    private val versionCheckModule: VersionCheckModule,
    private val ratingModule: RatingModule
  ) : AppSettingsComponent {

    override fun inject(fragment: AppSettingsPreferenceFragment) {
      val ratingViewModel =
        RatingViewModel(ratingModule.provideInteractor(), schedulerProvider, ratingBus)
      val versionViewModel = VersionCheckViewModel(
          versionCheckModule.provideInteractor(), schedulerProvider, versionCheckBus
      )
      val handler = AppSettingsHandler(schedulerProvider, settingsBus)
      val settingsViewModel = AppSettingsViewModel(handler, theming)
      val navigationViewModel = NavigationViewModel(schedulerProvider, navigationBus)
      val settingsView = AppSettingsView(
          applicationName, bugReportUrl, hideClearAll,
          hideUpgradeInformation, theming, preferenceScreen, handler
      )
      val component = AppSettingsUiComponentImpl(
          settingsView, versionViewModel,
          ratingViewModel, settingsViewModel,
          navigationViewModel
      )
      fragment._component = component
    }

    internal class FactoryImpl internal constructor(
      private val applicationName: String,
      private val bugReportUrl: String,
      private val theming: Theming,
      private val schedulerProvider: SchedulerProvider,
      private val settingsBus: EventBus<AppSettingsEvent>,
      private val versionCheckBus: EventBus<VersionCheckState>,
      private val ratingBus: EventBus<ShowRating>,
      private val navigationBus: EventBus<FailedNavigationEvent>,
      private val versionCheckModule: VersionCheckModule,
      private val ratingModule: RatingModule
    ) : Factory {

      override fun create(
        preferenceScreen: PreferenceScreen,
        hideClearAll: Boolean,
        hideUpgradeInformation: Boolean
      ): AppSettingsComponent {
        return Impl(
            applicationName, bugReportUrl, hideClearAll,
            hideUpgradeInformation, preferenceScreen,
            theming, schedulerProvider, settingsBus,
            versionCheckBus, ratingBus, navigationBus,
            versionCheckModule, ratingModule
        )
      }

    }
  }
}


