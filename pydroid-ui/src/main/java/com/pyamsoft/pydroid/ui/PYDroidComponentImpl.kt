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
import com.pyamsoft.pydroid.rating.RatingModule
import com.pyamsoft.pydroid.ui.about.AboutLibrariesComponent
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.social.SocialComponent
import com.pyamsoft.pydroid.ui.util.UtilComponent
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent
import com.pyamsoft.pydroid.version.VersionCheckModule

@RestrictTo(RestrictTo.Scope.LIBRARY) internal class PYDroidComponentImpl internal constructor(
    module: PYDroidModule) : PYDroidComponent {

  private val versionCheckComponent: VersionCheckComponent
  private val aboutLibrariesComponent: AboutLibrariesComponent
  private val appComponent: AppComponent
  private val ratingComponent: RatingComponent
  private val socialComponent: SocialComponent
  private val utilComponent: UtilComponent

  init {
    val versionCheckModule = VersionCheckModule(module)
    versionCheckComponent = VersionCheckComponent(versionCheckModule)
    appComponent = AppComponent(versionCheckModule)
    aboutLibrariesComponent = AboutLibrariesComponent(AboutLibrariesModule(module))
    ratingComponent = RatingComponent(RatingModule(module))
    socialComponent = SocialComponent(module)
    utilComponent = UtilComponent(module)
  }

  override fun plusVersionCheckComponent(): VersionCheckComponent {
    return versionCheckComponent
  }

  override fun plusAboutLibrariesComponent(): AboutLibrariesComponent {
    return aboutLibrariesComponent
  }

  override fun plusAppComponent(): AppComponent {
    return appComponent
  }

  override fun plusRatingComponent(): RatingComponent {
    return ratingComponent
  }

  override fun plusSocialComponent(): SocialComponent {
    return socialComponent
  }

  override fun plusUtilComponent(): UtilComponent {
    return utilComponent
  }
}
