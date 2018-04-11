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

import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.base.about.AboutLibrariesModule
import com.pyamsoft.pydroid.base.rating.RatingModule
import com.pyamsoft.pydroid.base.version.VersionCheckModule
import com.pyamsoft.pydroid.bus.RxBus
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.app.fragment.AppComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.rating.RatingComponentImpl
import com.pyamsoft.pydroid.ui.sec.TamperActivity
import com.pyamsoft.pydroid.ui.sec.TamperDialog
import com.pyamsoft.pydroid.ui.social.LinkerErrorPublisher
import com.pyamsoft.pydroid.ui.social.LinkerErrorPublisherImpl
import com.pyamsoft.pydroid.ui.social.SocialMediaLayout
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.version.VersionCheckComponentImpl
import com.pyamsoft.pydroid.ui.version.VersionUpgradeDialog

internal class PYDroidComponentImpl internal constructor(
  private val pyDroidModule: PYDroidModule,
  private val loaderModule: LoaderModule,
  private val uiModule: UiModule
) : PYDroidComponent {

  private val aboutLibrariesModule: AboutLibrariesModule = AboutLibrariesModule(pyDroidModule)
  private val versionCheckModule: VersionCheckModule = VersionCheckModule(pyDroidModule)
  private val ratingModule: RatingModule
  private val debugMode: Boolean = pyDroidModule.isDebug
  private val ratingErrorBus = RxBus.create<Throwable>()
  private val linkerErrorBus = RxBus.create<Throwable>()
  private val linkerErrorPublisher: LinkerErrorPublisher = LinkerErrorPublisherImpl(linkerErrorBus)

  init {
    val preferences = PYDroidPreferencesImpl(pyDroidModule.provideContext())
    ratingModule = RatingModule(pyDroidModule, preferences, ratingErrorBus)
  }

  override fun inject(fragment: AboutLibrariesFragment) {
    fragment.presenter = aboutLibrariesModule.getPresenter()
    fragment.imageLoader = loaderModule.provideImageLoader()
  }

  override fun inject(activity: TamperActivity) {
    activity.debugMode = debugMode
  }

  override fun inject(layout: SocialMediaLayout) {
    layout.imageLoader = loaderModule.provideImageLoader()
    layout.linker = uiModule.provideLinker()
    layout.linkerErrorPublisher = linkerErrorPublisher
  }

  override fun inject(tamperDialog: TamperDialog) {
    tamperDialog.linker = uiModule.provideLinker()
  }

  override fun inject(versionUpgradeDialog: VersionUpgradeDialog) {
    versionUpgradeDialog.linker = uiModule.provideLinker()
    versionUpgradeDialog.linkerErrorPublisher = linkerErrorPublisher
  }

  override fun plusVersionCheckComponent(
    packageName: String,
    currentVersion: Int
  ): VersionCheckComponent =
    VersionCheckComponentImpl(versionCheckModule, packageName, currentVersion)

  override fun plusAppComponent(
    packageName: String,
    currentVersion: Int
  ): AppComponent =
    AppComponentImpl(
        pyDroidModule, uiModule, versionCheckModule, ratingModule, linkerErrorBus,
        packageName, currentVersion
    )

  override fun plusRatingComponent(currentVersion: Int): RatingComponent =
    RatingComponentImpl(uiModule, ratingModule, loaderModule, ratingErrorBus, currentVersion)
}
