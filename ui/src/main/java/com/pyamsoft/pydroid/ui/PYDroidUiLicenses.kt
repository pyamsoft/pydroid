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

import com.pyamsoft.pydroid.core.PYDroidLicenses
import com.pyamsoft.pydroid.bootstrap.about.AboutLibraries
import com.pyamsoft.pydroid.list.PYDroidListLicenses

object PYDroidUiLicenses {

  @JvmStatic
  internal fun addLicenses() {
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

    AboutLibraries.create(
        PYDroidListLicenses.Names.RECYCLERVIEW,
        PYDroidListLicenses.HomepageUrls.RECYCLERVIEW,
        PYDroidListLicenses.LicenseLocations.RECYCLERVIEW
    )
  }

  object Names {
    const val CONSTRAINTLAYOUT = "Constraint Layout"
    const val DESIGN = "Material Design Support"
    const val SUPPORT_VECTOR = "Vector Support"
  }

  object HomepageUrls {
    const val CONSTRAINTLAYOUT = "https://source.android.com"
    const val DESIGN = "https://source.android.com"
    const val SUPPORT_VECTOR = "https://source.android.com"
  }

  object LicenseLocations {

    // Add an underscore to keep this name on top
    const val CONSTRAINTLAYOUT = PYDroidLicenses.LicenseLocations.__DIR + "constraintlayout"
    const val DESIGN = PYDroidLicenses.LicenseLocations.__DIR + "design"
    const val SUPPORT_VECTOR = PYDroidLicenses.LicenseLocations.__DIR + "support-vector"
  }
}
