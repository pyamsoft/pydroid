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

package com.pyamsoft.pydroid.ui.about

import com.pyamsoft.pydroid.base.about.Licenses

internal object UiLicenses {

    @JvmStatic
    internal fun addLicenses() {
        Licenses.create(
            Names.MATERIAL_VIEW_PAGER_INDICATOR,
            HomePageUrls.MATERIAL_VIEW_PAGER_INDICATOR,
            LicenseLocations.MATERIAL_VIEW_PAGER_INDICATOR
        )
    }

    private object Names {
        internal const val MATERIAL_VIEW_PAGER_INDICATOR = "Material ViewPagerIndicator"
    }

    private object HomePageUrls {
        internal const val MATERIAL_VIEW_PAGER_INDICATOR =
            "https://github.com/ronaldsmartin/Material-ViewPagerIndicator"
    }

    private object LicenseLocations {
        internal const val MATERIAL_VIEW_PAGER_INDICATOR = Licenses.LicenseLocations._BASE + "material-view-pager"
    }
}
