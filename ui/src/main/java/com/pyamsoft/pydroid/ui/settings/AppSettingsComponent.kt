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

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.ui.rating.RatingLoader
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.version.VersionView

internal interface AppSettingsComponent {

  fun inject(fragment: AppSettingsPreferenceFragment)

  interface Factory {

    @CheckResult
    fun create(
      parent: ViewGroup,
      owner: LifecycleOwner,
      preferenceScreen: PreferenceScreen,
      hideClearAll: Boolean,
      hideUpgradeInformation: Boolean
    ): AppSettingsComponent

  }

  class Impl private constructor(
    private val parent: ViewGroup,
    private val owner: LifecycleOwner,
    private val applicationName: String,
    private val bugReportUrl: String,
    private val hideClearAll: Boolean,
    private val hideUpgradeInformation: Boolean,
    private val preferenceScreen: PreferenceScreen,
    private val theming: Theming,
    private val schedulerProvider: SchedulerProvider,
    private val versionCheckModule: VersionCheckModule,
    private val ratingModule: RatingModule
  ) : AppSettingsComponent {

    override fun inject(fragment: AppSettingsPreferenceFragment) {
      val ratingViewModel = RatingLoader(ratingModule.provideInteractor(), schedulerProvider)
      val versionViewModel =
        VersionCheckViewModel(versionCheckModule.provideInteractor(), schedulerProvider)
      val versionView = VersionView(owner, parent)
      val settingsViewModel = AppSettingsViewModel(
          applicationName, bugReportUrl, hideClearAll, hideUpgradeInformation, theming
      )
      val settingsView = AppSettingsView(preferenceScreen)

      fragment.versionView = versionView
      fragment.versionViewModel = versionViewModel

      fragment.ratingLoader = ratingViewModel

      fragment.appSettingsView = settingsView
      fragment.appSettingsViewModel = settingsViewModel
    }

    internal class FactoryImpl internal constructor(
      private val applicationName: String,
      private val bugReportUrl: String,
      private val theming: Theming,
      private val schedulerProvider: SchedulerProvider,
      private val versionCheckModule: VersionCheckModule,
      private val ratingModule: RatingModule
    ) : Factory {

      override fun create(
        parent: ViewGroup,
        owner: LifecycleOwner,
        preferenceScreen: PreferenceScreen,
        hideClearAll: Boolean,
        hideUpgradeInformation: Boolean
      ): AppSettingsComponent {
        return Impl(
            parent, owner, applicationName, bugReportUrl, hideClearAll, hideUpgradeInformation,
            preferenceScreen, theming, schedulerProvider, versionCheckModule, ratingModule
        )
      }

    }
  }
}


