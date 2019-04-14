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
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.AboutComponent
import com.pyamsoft.pydroid.ui.about.AboutComponentImpl
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponent
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponentImpl
import com.pyamsoft.pydroid.ui.app.ToolbarActivity
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationModule
import com.pyamsoft.pydroid.ui.rating.RatingActivity
import com.pyamsoft.pydroid.ui.rating.RatingUiComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.rating.ShowRating
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponent
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponentImpl
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponent
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponentImpl
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckState
import com.pyamsoft.pydroid.ui.version.VersionComponent
import com.pyamsoft.pydroid.ui.version.VersionComponentImpl
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponentImpl
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeHandler.VersionHandlerEvent

internal class PYDroidComponentImpl internal constructor(
  application: Application,
  private val debug: Boolean,
  private val applicationName: String,
  private val bugReportUrl: String,
  private val currentVersion: Int,
  private val schedulerProvider: SchedulerProvider
) : PYDroidComponent, ModuleProvider {

  private val ratingStateBus = RxBus.create<ShowRating>()
  private val versionStateBus = RxBus.create<VersionCheckState>()
  private val versionHandlerBus = RxBus.create<VersionHandlerEvent>()

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
    val presenter = RatingViewModel(ratingModule.interactor, schedulerProvider, ratingStateBus)

    activity.apply {
      this.ratingComponent = RatingUiComponentImpl(presenter)
    }
  }

  override fun plusVersionComponent(
    owner: LifecycleOwner,
    view: View
  ): VersionComponent = VersionComponentImpl(
      owner, view, versionStateBus, versionModule.interactor, schedulerProvider,
      navigationModule.bus
  )

  override fun plusAboutItemComponent(
    parent: ViewGroup,
    model: OssLibrary
  ): AboutItemComponent =
    AboutItemComponentImpl(parent, model)

  override fun plusVersionUpgradeComponent(
    parent: ViewGroup,
    newVersion: Int
  ): VersionUpgradeComponent = VersionUpgradeComponentImpl(
      parent, applicationName, currentVersion, newVersion, schedulerProvider,
      navigationModule.bus, versionHandlerBus
  )

  override fun plusSettingsComponent(
    preferenceScreen: PreferenceScreen,
    hideClearAll: Boolean,
    hideUpgradeInformation: Boolean
  ): AppSettingsComponent = AppSettingsComponentImpl(
      ratingModule.interactor, versionModule.interactor, theming,
      versionStateBus, ratingStateBus, schedulerProvider, preferenceScreen,
      applicationName, bugReportUrl, hideClearAll, hideUpgradeInformation, navigationModule.bus
  )

  override fun plusAboutComponent(
    owner: LifecycleOwner,
    toolbarActivity: ToolbarActivity,
    backstackCount: Int,
    parent: ViewGroup
  ): AboutComponent = AboutComponentImpl(
      aboutModule.interactor, toolbarActivity, backstackCount, parent, owner,
      navigationModule.bus, schedulerProvider
  )

  override fun plusRatingDialogComponent(
    parent: ViewGroup,
    rateLink: String,
    changelogIcon: Int,
    changelog: SpannedString
  ): RatingDialogComponent = RatingDialogComponentImpl(
      ratingModule.interactor, loaderModule.provideImageLoader(), schedulerProvider,
      parent, rateLink, changelogIcon, changelog,
      navigationModule.bus
  )

  override fun schedulerProvider(): SchedulerProvider {
    return schedulerProvider
  }

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
