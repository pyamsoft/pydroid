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

package com.pyamsoft.pydroid.ui.about

import com.pyamsoft.pydroid.about.Licenses

internal object UiLicenses {

  @JvmStatic internal fun addLicenses() {
    Licenses.create(Names.MATERIAL_VIEW_PAGER_INDICATOR, HomePageUrls.MATERIAL_VIEW_PAGER_INDICATOR,
        LicenseLocations.MATERIAL_VIEW_PAGER_INDICATOR)
  }

  private object Names {
    internal const val MATERIAL_VIEW_PAGER_INDICATOR = "Material ViewPagerIndicator"
  }

  private object HomePageUrls {
    internal const val MATERIAL_VIEW_PAGER_INDICATOR = "https://github.com/ronaldsmartin/Material-ViewPagerIndicator"
  }

  private object LicenseLocations {
    internal const val MATERIAL_VIEW_PAGER_INDICATOR = Licenses.LicenseLocations._BASE + "material-view-pager"
  }

}

