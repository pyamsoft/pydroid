/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui

import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.about.AboutLibrariesModule
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.app.fragment.AppComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.rating.RatingComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingModule
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.version.VersionCheckComponentImpl
import com.pyamsoft.pydroid.version.VersionCheckModule

internal class PYDroidComponentImpl internal constructor(module: PYDroidModule) : PYDroidComponent {
  private val aboutLibrariesModule: AboutLibrariesModule = AboutLibrariesModule(module)
  private val versionCheckModule: VersionCheckModule = VersionCheckModule(module)
  private val ratingModule: RatingModule

  init {
    val preferences = PYDroidPreferencesImpl(module.provideContext())
    ratingModule = RatingModule(module, preferences)
  }

  override fun inject(fragment: AboutLibrariesFragment) {
    fragment.presenter = aboutLibrariesModule.getPresenter()
  }

  override fun plusVersionCheckComponent(packageName: String,
      currentVersion: Int): VersionCheckComponent =
      VersionCheckComponentImpl(versionCheckModule, packageName, currentVersion)

  override fun plusAppComponent(packageName: String, currentVersion: Int): AppComponent =
      AppComponentImpl(versionCheckModule, packageName, currentVersion)

  override fun plusRatingComponent(currentVersion: Int): RatingComponent =
      RatingComponentImpl(currentVersion, ratingModule)
}
