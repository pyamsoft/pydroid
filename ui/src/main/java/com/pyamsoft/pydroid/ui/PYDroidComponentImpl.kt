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
import com.pyamsoft.pydroid.ui.about.LicenseLoadState
import com.pyamsoft.pydroid.ui.about.dialog.UrlComponent
import com.pyamsoft.pydroid.ui.about.dialog.UrlComponentImpl
import com.pyamsoft.pydroid.ui.about.dialog.UrlWebviewState
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponent
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponentImpl
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationModule
import com.pyamsoft.pydroid.ui.rating.RatingActivity
import com.pyamsoft.pydroid.ui.rating.RatingPresenterImpl
import com.pyamsoft.pydroid.ui.rating.ShowRating
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponent
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponentImpl
import com.pyamsoft.pydroid.ui.rating.dialog.RatingSavedEvent
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponent
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponentImpl
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckState
import com.pyamsoft.pydroid.ui.version.VersionComponent
import com.pyamsoft.pydroid.ui.version.VersionComponentImpl
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponentImpl

internal class PYDroidComponentImpl internal constructor(
  debug: Boolean,
  application: Application,
  private val applicationName: String,
  private val bugreportUrl: String,
  private val currentVersion: Int,
  private val schedulerProvider: SchedulerProvider
) : PYDroidComponent, ModuleProvider {

  private val ratingStateBus = RxBus.create<ShowRating>()

  private val ratingDialogStateBus = RxBus.create<RatingSavedEvent>()

  private val versionStateBus = RxBus.create<VersionCheckState>()

  private val aboutStateBus = RxBus.create<LicenseLoadState>()

  private val webviewStateBus = RxBus.create<UrlWebviewState>()

  private val preferences = PYDroidPreferencesImpl(application)
  private val enforcer by lazy { Enforcer(debug) }
  private val theming by lazy { Theming(application) }
  private val loaderModule by lazy { LoaderModule() }
  private val aboutModule by lazy { AboutModule(enforcer) }
  private val ratingModule by lazy { RatingModule(preferences, enforcer, currentVersion) }
  private val versionModule by lazy {
    VersionCheckModule(application, enforcer, debug, currentVersion)
  }
  private val navigationModule by lazy { FailedNavigationModule() }

  override fun inject(activity: RatingActivity) {
    val presenter = RatingPresenterImpl(ratingModule.interactor, schedulerProvider, ratingStateBus)

    activity.apply {
      this.ratingPresenter = presenter
    }
  }

  override fun plusVersionComponent(
    owner: LifecycleOwner,
    view: View
  ): VersionComponent = VersionComponentImpl(
      owner, view, versionStateBus, versionModule.interactor, schedulerProvider,
      navigationModule.bus
  )

  override fun plusAboutItemComponent(parent: ViewGroup): AboutItemComponent =
    AboutItemComponentImpl(parent, navigationModule.bus)

  override fun plusVersionUpgradeComponent(
    parent: ViewGroup,
    newVersion: Int
  ): VersionUpgradeComponent = VersionUpgradeComponentImpl(
      parent, applicationName, currentVersion, newVersion, navigationModule.bus
  )

  override fun plusSettingsComponent(
    preferenceScreen: PreferenceScreen,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean
  ): AppSettingsComponent = AppSettingsComponentImpl(
      ratingModule.interactor, versionModule.interactor, theming,
      versionStateBus, ratingStateBus, schedulerProvider, preferenceScreen,
      applicationName, bugreportUrl, hideClearAll, hideUpgradeInformation, navigationModule.bus
  )

  override fun plusAboutComponent(
    owner: LifecycleOwner,
    parent: ViewGroup
  ): AboutComponent = AboutComponentImpl(
      aboutModule.interactor, parent, owner, aboutStateBus, schedulerProvider
  )

  override fun plusViewLicenseComponent(
    owner: LifecycleOwner,
    parent: ViewGroup,
    link: String,
    name: String
  ): UrlComponent = UrlComponentImpl(
      parent, owner, loaderModule.provideImageLoader(),
      link, name, webviewStateBus, navigationModule.bus
  )

  override fun plusRatingDialogComponent(
    parent: ViewGroup,
    rateLink: String,
    changelogIcon: Int,
    changelog: SpannedString
  ): RatingDialogComponent = RatingDialogComponentImpl(
      ratingModule.interactor, loaderModule.provideImageLoader(), schedulerProvider,
      parent, rateLink, changelogIcon, changelog,
      ratingDialogStateBus, navigationModule.bus
  )

  override fun enforcer(): Enforcer {
    return enforcer
  }

  override fun theming(): Theming {
    return theming
  }

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

  override fun failedNavigationModule(): FailedNavigationModule {
    return navigationModule
  }
}
