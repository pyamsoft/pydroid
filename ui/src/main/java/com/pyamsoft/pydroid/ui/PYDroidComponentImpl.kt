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
import android.text.SpannedString
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.AboutComponent
import com.pyamsoft.pydroid.ui.about.AboutComponentImpl
import com.pyamsoft.pydroid.ui.about.AboutStateEvent
import com.pyamsoft.pydroid.ui.about.AboutViewEvent
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent
import com.pyamsoft.pydroid.ui.about.dialog.LicenseViewEvent
import com.pyamsoft.pydroid.ui.about.dialog.ViewLicenseComponent
import com.pyamsoft.pydroid.ui.about.dialog.ViewLicenseComponentImpl
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponent
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.rating.RatingComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingStateEvent
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponent
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponentImpl
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogStateEvent
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponent
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponentImpl
import com.pyamsoft.pydroid.ui.settings.AppSettingsStateEvent
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewEvent
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionComponent
import com.pyamsoft.pydroid.ui.version.VersionComponentImpl
import com.pyamsoft.pydroid.ui.version.VersionStateEvent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponentImpl
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeStateEvent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewEvent

internal class PYDroidComponentImpl internal constructor(
  debug: Boolean,
  application: Application,
  private val applicationName: String,
  private val bugreportUrl: String,
  private val currentVersion: Int,
  private val schedulerProvider: SchedulerProvider
) : PYDroidComponent, ModuleProvider {

  private val ratingStateBus = RxBus.create<RatingStateEvent>()
  private val ratingViewBus = RxBus.create<RatingDialogViewEvent>()

  private val ratingDialogStateBus = RxBus.create<RatingDialogStateEvent>()

  private val versionStateBus = RxBus.create<VersionStateEvent>()

  private val versionUpgradeViewBus = RxBus.create<VersionUpgradeViewEvent>()
  private val versionUpgradeStateBus = RxBus.create<VersionUpgradeStateEvent>()

  private val aboutStateBus = RxBus.create<AboutStateEvent>()
  private val aboutViewBus = RxBus.create<AboutViewEvent>()

  private val licenseViewBus = RxBus.create<LicenseViewEvent>()
  private val licenseStateBus = RxBus.create<LicenseStateEvent>()

  private val settingsViewBus = RxBus.create<AppSettingsViewEvent>()
  private val settingsStateBus = RxBus.create<AppSettingsStateEvent>()

  private val preferences = PYDroidPreferencesImpl(application)
  private val enforcer by lazy { Enforcer(debug) }
  private val theming by lazy { Theming(application) }
  private val loaderModule by lazy { LoaderModule() }
  private val aboutModule by lazy { AboutModule(enforcer) }
  private val ratingModule by lazy { RatingModule(preferences, enforcer, currentVersion) }
  private val versionModule by lazy {
    VersionCheckModule(application, enforcer, debug, currentVersion)
  }

  override fun enforcer(): Enforcer {
    return enforcer
  }

  override fun theming(): Theming {
    return theming
  }

  override fun plusVersionComponent(
    owner: LifecycleOwner,
    view: View
  ): VersionComponent = VersionComponentImpl(
      owner, view, versionStateBus, versionUpgradeStateBus,
      versionModule.interactor, schedulerProvider
  )

  override fun plusAboutItemComponent(
    owner: LifecycleOwner,
    parent: ViewGroup
  ): AboutItemComponent =
    AboutItemComponentImpl(parent, owner, aboutViewBus)

  override fun plusVersionUpgradeComponent(
    owner: LifecycleOwner,
    parent: ViewGroup,
    newVersion: Int
  ): VersionUpgradeComponent = VersionUpgradeComponentImpl(
      owner, parent, applicationName, currentVersion,
      newVersion, versionUpgradeViewBus, versionUpgradeStateBus
  )

  override fun plusSettingsComponent(
    view: View,
    owner: LifecycleOwner,
    preferenceScreen: PreferenceScreen,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean
  ): AppSettingsComponent = AppSettingsComponentImpl(
      view, owner, ratingModule, versionModule, theming,
      versionStateBus, ratingStateBus, settingsViewBus, settingsStateBus,
      schedulerProvider, preferenceScreen, applicationName,
      bugreportUrl, hideClearAll, hideUpgradeInformation
  )

  override fun plusAboutComponent(
    owner: LifecycleOwner,
    parent: ViewGroup
  ): AboutComponent = AboutComponentImpl(
      aboutModule, parent, owner, aboutStateBus, licenseStateBus, aboutViewBus, schedulerProvider
  )

  override fun plusViewLicenseComponent(
    owner: LifecycleOwner,
    parent: ViewGroup,
    link: String,
    name: String
  ): ViewLicenseComponent = ViewLicenseComponentImpl(
      parent, owner, loaderModule.provideImageLoader(),
      link, name, licenseViewBus, licenseStateBus, schedulerProvider
  )

  override fun plusRatingDialogComponent(
    owner: LifecycleOwner,
    parent: ViewGroup,
    rateLink: String,
    changelogIcon: Int,
    changelog: SpannedString
  ): RatingDialogComponent = RatingDialogComponentImpl(
      ratingModule, loaderModule, schedulerProvider,
      parent, owner, rateLink, changelogIcon, changelog,
      ratingViewBus, ratingDialogStateBus
  )

  override fun plusRatingComponent(
    owner: LifecycleOwner,
    view: View
  ): RatingComponent = RatingComponentImpl(
      owner, view, ratingStateBus, ratingDialogStateBus,
      ratingModule.interactor, schedulerProvider
  )

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
