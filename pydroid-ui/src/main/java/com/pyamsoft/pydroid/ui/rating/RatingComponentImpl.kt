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

package com.pyamsoft.pydroid.ui.rating

import com.pyamsoft.pydroid.loader.LoaderModule

internal class RatingComponentImpl internal constructor(
    private val version: Int,
    private val ratingModule: RatingModule,
    private val loaderModule: LoaderModule
) : RatingComponent {

    override fun inject(activity: RatingActivity) {
        activity.ratingPresenter = ratingModule.getPresenter(version)
    }

    override fun inject(dialog: RatingDialog) {
        dialog.presenter = ratingModule.getSavePresenter(version)
        dialog.imageLoader = loaderModule.provideImageLoader()
    }
}
