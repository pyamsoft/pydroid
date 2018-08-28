/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui

import android.app.Application
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutLibrariesModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.app.fragment.AppComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.rating.RatingComponentImpl
import com.pyamsoft.pydroid.ui.social.SocialMediaPreference
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.version.VersionCheckComponentImpl
import com.pyamsoft.pydroid.ui.version.VersionUpgradeDialog

internal class PYDroidComponentImpl internal constructor(
  application: Application,
  debug: Boolean,
  private val schedulerProvider: SchedulerProvider
) : PYDroidComponent, ModuleProvider {

  private val enforcer = Enforcer(debug)
  private val loaderModule = LoaderModule(application, enforcer)
  private val uiModule = UiModule(application)
  private val aboutLibrariesModule = AboutLibrariesModule(application, enforcer, schedulerProvider)
  private val versionCheckModule =
    VersionCheckModule(application, enforcer, debug, schedulerProvider)
  private val ratingModule: RatingModule

  init {
    val preferences = PYDroidPreferencesImpl(application)
    ratingModule = RatingModule(preferences, enforcer, schedulerProvider)
  }

  override fun enforcer(): Enforcer {
    return enforcer
  }

  override fun inject(fragment: AboutLibrariesFragment) {
    fragment.viewModel = aboutLibrariesModule.getViewModel()
    fragment.imageLoader = loaderModule.provideImageLoader()
  }

  override fun inject(layout: SocialMediaPreference) {
    layout.imageLoader = loaderModule.provideImageLoader()
    layout.linker = uiModule.provideLinker()
    layout.linkerErrorPublisher = uiModule.provideLinkerErrorBus()
  }

  override fun inject(versionUpgradeDialog: VersionUpgradeDialog) {
    versionUpgradeDialog.linker = uiModule.provideLinker()
    versionUpgradeDialog.linkerErrorPublisher = uiModule.provideLinkerErrorBus()
  }

  override fun plusVersionCheckComponent(currentVersion: Int): VersionCheckComponent =
    VersionCheckComponentImpl(versionCheckModule, currentVersion)

  override fun plusAppComponent(currentVersion: Int): AppComponent =
    AppComponentImpl(uiModule, versionCheckModule, ratingModule, currentVersion, schedulerProvider)

  override fun plusRatingComponent(currentVersion: Int): RatingComponent =
    RatingComponentImpl(uiModule, ratingModule, loaderModule, currentVersion)

  override fun loaderModule(): LoaderModule {
    return loaderModule
  }

  override fun ratingModule(): RatingModule {
    return ratingModule
  }

  override fun aboutLibrariesModule(): AboutLibrariesModule {
    return aboutLibrariesModule
  }

  override fun versionCheckModule(): VersionCheckModule {
    return versionCheckModule
  }

  override fun uiModule(): UiModule {
    return uiModule
  }
}
