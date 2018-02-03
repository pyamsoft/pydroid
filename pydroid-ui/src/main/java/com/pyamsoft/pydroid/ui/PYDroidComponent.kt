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

import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.sec.TamperActivity
import com.pyamsoft.pydroid.ui.social.SocialMediaLayout
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent

internal interface PYDroidComponent {

  fun inject(fragment: AboutLibrariesFragment)

  fun inject(activity: TamperActivity)

  fun inject(layout: SocialMediaLayout)

  @CheckResult
  fun plusVersionCheckComponent(
    packageName: String,
    currentVersion: Int
  ): VersionCheckComponent

  @CheckResult
  fun plusAppComponent(
    packageName: String,
    currentVersion: Int
  ): AppComponent

  @CheckResult
  fun plusRatingComponent(currentVersion: Int): RatingComponent
}
