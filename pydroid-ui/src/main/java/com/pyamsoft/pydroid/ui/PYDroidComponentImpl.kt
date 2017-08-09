/*
 * Copyright 2017 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui

import android.support.annotation.RestrictTo
import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.about.AboutLibrariesModule
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.about.AboutLibrariesViewPresenter
import com.pyamsoft.pydroid.ui.about.AboutPagerFragment
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.app.fragment.AppComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.rating.RatingComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingModule
import com.pyamsoft.pydroid.ui.social.Linker
import com.pyamsoft.pydroid.ui.util.AnimUtil
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.version.VersionCheckComponentImpl
import com.pyamsoft.pydroid.version.VersionCheckModule

@RestrictTo(RestrictTo.Scope.LIBRARY) internal class PYDroidComponentImpl internal constructor(
    private val module: PYDroidModule) : PYDroidComponent {
  private val aboutLibrariesModule: AboutLibrariesModule = AboutLibrariesModule(module)
  private val versionCheckModule: VersionCheckModule = VersionCheckModule(module)
  private val ratingModule: RatingModule

  init {
    val preferences = PYDroidPreferencesImpl(module.provideContext())
    ratingModule = RatingModule(module, preferences)
  }

  override fun inject(fragment: AboutPagerFragment) {
    fragment.presenter = AboutLibrariesViewPresenter()
  }

  override fun inject(fragment: AboutLibrariesFragment) {
    fragment.presenter = aboutLibrariesModule.getPresenter()
    fragment.viewPresenter = AboutLibrariesViewPresenter()
  }

  override fun inject(animUtil: AnimUtil) {
    animUtil.context = module.provideContext().applicationContext
  }

  override fun inject(linker: Linker) {
    linker.appContext = module.provideContext().applicationContext
  }

  override fun plusVersionCheckComponent(packageName: String,
      currentVersion: Int): VersionCheckComponent {
    return VersionCheckComponentImpl(versionCheckModule, packageName, currentVersion)
  }

  override fun plusAppComponent(packageName: String, currentVersion: Int): AppComponent {
    return AppComponentImpl(versionCheckModule, packageName, currentVersion)
  }

  override fun plusRatingComponent(currentVersion: Int): RatingComponent {
    return RatingComponentImpl(currentVersion, ratingModule)
  }
}
