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

import com.pyamsoft.pydroid.PYDroidLicenses
import com.pyamsoft.pydroid.base.about.AboutLibraries

object PYDroidUiLicenses {

  @JvmStatic
  internal fun addLicenses() {
    AboutLibraries.create(
        Names.VIEW_PAGER_INDICATOR,
        HomepageUrls.VIEW_PAGER_INDICATOR,
        LicenseLocations.VIEW_PAGER_INDICATOR
    )

    AboutLibraries.create(
        Names.CARDVIEW,
        HomepageUrls.CARDVIEW,
        LicenseLocations.CARDVIEW
    )

    AboutLibraries.create(
        Names.CONSTRAINTLAYOUT,
        HomepageUrls.CONSTRAINTLAYOUT,
        LicenseLocations.CONSTRAINTLAYOUT
    )

    AboutLibraries.create(
        Names.DESIGN,
        HomepageUrls.DESIGN,
        LicenseLocations.DESIGN
    )

    AboutLibraries.create(
        Names.SUPPORT_VECTOR,
        HomepageUrls.SUPPORT_VECTOR,
        LicenseLocations.SUPPORT_VECTOR
    )
  }

  object Names {
    const val CARDVIEW = "CardView v7"
    const val CONSTRAINTLAYOUT = "Constraint Layout"
    const val DESIGN = "Design Support"
    const val SUPPORT_VECTOR = "Vector Support"
    const val VIEW_PAGER_INDICATOR = "Material ViewPagerIndicator"
  }

  object HomepageUrls {
    const val CARDVIEW = "https://source.android.com"
    const val CONSTRAINTLAYOUT = "https://source.android.com"
    const val DESIGN = "https://source.android.com"
    const val SUPPORT_VECTOR = "https://source.android.com"
    const val VIEW_PAGER_INDICATOR = "https://github.com/ronaldsmartin/Material-ViewPagerIndicator"
  }

  object LicenseLocations {

    // Add an underscore to keep this name on top
    const val CARDVIEW = PYDroidLicenses.LicenseLocations.__DIR + "cardview-v7"
    const val CONSTRAINTLAYOUT = PYDroidLicenses.LicenseLocations.__DIR + "constraintlayout"
    const val DESIGN = PYDroidLicenses.LicenseLocations.__DIR + "design"
    const val SUPPORT_VECTOR = PYDroidLicenses.LicenseLocations.__DIR + "support-vector"
    const val VIEW_PAGER_INDICATOR = PYDroidLicenses.LicenseLocations.__DIR + "material-view-pager"
  }
}
