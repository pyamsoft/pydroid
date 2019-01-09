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

package com.pyamsoft.pydroid.ui

import android.app.Application
import android.os.Bundle
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingEvents
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.AboutComponent
import com.pyamsoft.pydroid.ui.about.AboutComponentImpl
import com.pyamsoft.pydroid.ui.about.ViewLicenseComponent
import com.pyamsoft.pydroid.ui.about.ViewLicenseComponentImpl
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.app.fragment.AppComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingActivity
import com.pyamsoft.pydroid.ui.rating.RatingDialogComponent
import com.pyamsoft.pydroid.ui.rating.RatingDialogComponentImpl
import com.pyamsoft.pydroid.ui.settings.SettingsPreferenceComponent
import com.pyamsoft.pydroid.ui.settings.SettingsPreferenceComponentImpl
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckActivity
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenter
import com.pyamsoft.pydroid.ui.version.VersionEvents
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponentImpl
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvents

internal class PYDroidComponentImpl internal constructor(
  debug: Boolean,
  application: Application,
  private val applicationName: String,
  private val bugreportUrl: String,
  private val currentVersion: Int,
  private val schedulerProvider: SchedulerProvider
) : PYDroidComponent, ModuleProvider {

  private val showRatingBus = RxBus.create<RatingEvents.ShowEvent>()
  private val showRatingErrorBus = RxBus.create<RatingEvents.ShowErrorEvent>()
  private val ratingSaveErrorBus = RxBus.create<RatingEvents.SaveErrorEvent>()

  private val versionCheckBus = RxBus.create<VersionEvents>()
  private val versionUpgradeBus = RxBus.create<VersionUpgradeViewEvents>()

  private val preferences = PYDroidPreferencesImpl(application)
  private val enforcer by lazy { Enforcer(debug) }
  private val theming by lazy { Theming(application) }
  private val loaderModule by lazy { LoaderModule() }
  private val aboutModule by lazy { AboutModule(enforcer, schedulerProvider) }
  private val ratingModule by lazy {
    RatingModule(
        preferences, enforcer, currentVersion, showRatingBus,
        showRatingErrorBus, ratingSaveErrorBus, schedulerProvider
    )
  }
  private val versionModule by lazy {
    VersionCheckModule(application, enforcer, debug, currentVersion)
  }

  override fun enforcer(): Enforcer {
    return enforcer
  }

  override fun theming(): Theming {
    return theming
  }

  override fun inject(activity: RatingActivity) {
    activity.ratingViewModel = ratingModule.getViewModel()
  }

  override fun inject(activity: VersionCheckActivity) {
    activity.presenter = VersionCheckPresenter(
        versionModule.interactor, versionCheckBus, schedulerProvider
    )
  }

  override fun plusVersionUpgradeComponent(
    parent: ViewGroup,
    newVersion: Int
  ): VersionUpgradeComponent = VersionUpgradeComponentImpl(
      parent, applicationName, currentVersion, newVersion,
      versionUpgradeBus, schedulerProvider
  )

  override fun plusSettingsComponent(
    owner: LifecycleOwner,
    preferenceScreen: PreferenceScreen,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean
  ): SettingsPreferenceComponent =
    SettingsPreferenceComponentImpl(
        ratingModule, versionModule, theming,
        versionCheckBus, schedulerProvider,
        owner, preferenceScreen, applicationName,
        bugreportUrl, hideClearAll, hideUpgradeInformation
    )

  override fun plusAboutComponent(
    owner: LifecycleOwner,
    activity: FragmentActivity,
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): AboutComponent = AboutComponentImpl(
      aboutModule, loaderModule.provideImageLoader(),
      owner, activity, inflater, container, savedInstanceState
  )

  override fun plusViewLicenseComponent(
    owner: LifecycleOwner,
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
    link: String,
    name: String
  ): ViewLicenseComponent {
    return ViewLicenseComponentImpl(
        inflater, container, owner, loaderModule.provideImageLoader(), link, name
    )
  }

  override fun plusRatingDialogComponent(
    owner: LifecycleOwner,
    inflater: LayoutInflater,
    container: ViewGroup?,
    changeLogIcon: Int,
    changeLog: SpannedString
  ): RatingDialogComponent = RatingDialogComponentImpl(
      ratingModule, loaderModule, inflater, container, owner, changeLogIcon, changeLog
  )

  override fun plusAppComponent(
    owner: LifecycleOwner,
    inflater: LayoutInflater,
    container: ViewGroup?
  ): AppComponent = AppComponentImpl(owner, inflater, container)

  override fun loaderModule(): LoaderModule {
    return loaderModule
  }

  override fun ratingModule(): RatingModule {
    return ratingModule
  }

  override fun aboutModule(): AboutModule {
    return aboutModule
  }

  override fun versionCheckModule(): VersionCheckModule {
    return versionModule
  }
}
