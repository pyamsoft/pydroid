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
import com.pyamsoft.pydroid.base.about.AboutLibrariesModule
import com.pyamsoft.pydroid.base.version.VersionCheckModule
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment
import com.pyamsoft.pydroid.ui.app.fragment.AppComponent
import com.pyamsoft.pydroid.ui.app.fragment.AppComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingComponent
import com.pyamsoft.pydroid.ui.rating.RatingComponentImpl
import com.pyamsoft.pydroid.ui.rating.RatingModule
import com.pyamsoft.pydroid.ui.sec.TamperActivity
import com.pyamsoft.pydroid.ui.social.SocialMediaLayout
import com.pyamsoft.pydroid.ui.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.version.VersionCheckComponentImpl

internal class PYDroidComponentImpl internal constructor(
    pyDroidModule: PYDroidModule,
    private val loaderModule: LoaderModule
) : PYDroidComponent {
    private val aboutLibrariesModule: AboutLibrariesModule = AboutLibrariesModule(
        pyDroidModule
    )
    private val versionCheckModule: VersionCheckModule = VersionCheckModule(
        pyDroidModule
    )
    private val ratingModule: RatingModule
    private val debugMode: Boolean = pyDroidModule.isDebug

    init {
        val preferences = PYDroidPreferencesImpl(pyDroidModule.provideContext())
        ratingModule = RatingModule(pyDroidModule, preferences)
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
    }

    override fun plusVersionCheckComponent(
        packageName: String,
        currentVersion: Int
    ): VersionCheckComponent =
        VersionCheckComponentImpl(versionCheckModule, packageName, currentVersion)

    override fun plusAppComponent(packageName: String, currentVersion: Int): AppComponent =
        AppComponentImpl(versionCheckModule, ratingModule, packageName, currentVersion)

    override fun plusRatingComponent(currentVersion: Int): RatingComponent =
        RatingComponentImpl(currentVersion, ratingModule, loaderModule)
}
