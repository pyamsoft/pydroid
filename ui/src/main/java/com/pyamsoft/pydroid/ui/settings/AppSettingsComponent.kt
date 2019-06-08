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

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.ui.PYDroidViewModelFactory
import com.pyamsoft.pydroid.ui.rating.RatingLoader
import com.pyamsoft.pydroid.ui.version.VersionView

internal interface AppSettingsComponent {

  fun inject(fragment: AppSettingsPreferenceFragment)

  interface Factory {

    @CheckResult
    fun create(
      activity: Activity,
      parent: ViewGroup,
      owner: LifecycleOwner,
      preferenceScreen: PreferenceScreen,
      hideClearAll: Boolean,
      hideUpgradeInformation: Boolean
    ): AppSettingsComponent

  }

  class Impl private constructor(
    private val activity: Activity,
    private val parent: ViewGroup,
    private val owner: LifecycleOwner,
    private val applicationName: String,
    private val bugReportUrl: String,
    private val hideClearAll: Boolean,
    private val hideUpgradeInformation: Boolean,
    private val preferenceScreen: PreferenceScreen,
    private val ratingModule: RatingModule,
    private val schedulerProvider: SchedulerProvider,
    private val factoryProvider: (activity: Activity) -> PYDroidViewModelFactory
  ) : AppSettingsComponent {

    override fun inject(fragment: AppSettingsPreferenceFragment) {
      val versionView = VersionView(owner, parent)
      val settingsView = AppSettingsView(
          applicationName, bugReportUrl, hideClearAll, hideUpgradeInformation, preferenceScreen
      )

      fragment.versionView = versionView
      fragment.appSettingsView = settingsView
      fragment.ratingLoader = RatingLoader(ratingModule.provideInteractor(), schedulerProvider)
      fragment.appSettingsViewModelFactory = factoryProvider(activity)
    }

    internal class FactoryImpl internal constructor(
      private val applicationName: String,
      private val bugReportUrl: String,
      private val ratingModule: RatingModule,
      private val schedulerProvider: SchedulerProvider,
      private val factoryProvider: (activity: Activity) -> PYDroidViewModelFactory
    ) : Factory {

      override fun create(
        activity: Activity,
        parent: ViewGroup,
        owner: LifecycleOwner,
        preferenceScreen: PreferenceScreen,
        hideClearAll: Boolean,
        hideUpgradeInformation: Boolean
      ): AppSettingsComponent {
        return Impl(
            activity, parent, owner, applicationName, bugReportUrl,
            hideClearAll, hideUpgradeInformation, preferenceScreen,
            ratingModule, schedulerProvider, factoryProvider
        )
      }

    }
  }
}


