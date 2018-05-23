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

package com.pyamsoft.pydroid.base.about

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.PYDroidLicenses
import com.pyamsoft.pydroid.util.PYDroidUtilLicenses
import java.util.Collections

object AboutLibraries {

  private val licenses: MutableSet<AboutLibrariesModel> = LinkedHashSet()

  init {
    addCommonLicenses()
  }

  /**
   * These libraries are directly used by PYDroid, and are thus in every application that uses
   * pydroid
   */
  private fun addCommonLicenses() {
    createItem(
        PYDroidLicenses.Names.ANDROID,
        PYDroidLicenses.HomepageUrls.ANDROID,
        PYDroidLicenses.LicenseLocations.ANDROID
    )
    createItem(
        PYDroidLicenses.Names.ARCH_SUPPORT,
        PYDroidLicenses.HomepageUrls.ARCH_SUPPORT,
        PYDroidLicenses.LicenseLocations.ARCH_SUPPORT
    )
    createItem(
        PYDroidLicenses.Names.TIMBER,
        PYDroidLicenses.HomepageUrls.TIMBER,
        PYDroidLicenses.LicenseLocations.TIMBER
    )
    createItem(
        PYDroidLicenses.Names.GRADLE_VERSIONS_PLUGIN,
        PYDroidLicenses.HomepageUrls.GRADLE_VERSIONS_PLUGIN,
        PYDroidLicenses.LicenseLocations.GRADLE_VERSIONS_PLUGIN
    )
    createItem(
        PYDroidLicenses.Names.DEXCOUNT_GRADLE_PLUGIN,
        PYDroidLicenses.HomepageUrls.DEXCOUNT_GRADLE_PLUGIN,
        PYDroidLicenses.LicenseLocations.DEXCOUNT_GRADLE_PLUGIN
    )
    createItem(
        PYDroidLicenses.Names.RXJAVA,
        PYDroidLicenses.HomepageUrls.RXJAVA,
        PYDroidLicenses.LicenseLocations.RXJAVA
    )
    createItem(
        PYDroidLicenses.Names.KOTLIN,
        PYDroidLicenses.HomepageUrls.KOTLIN,
        PYDroidLicenses.LicenseLocations.KOTLIN
    )
    createItem(
        PYDroidLicenses.Names.KTX,
        PYDroidLicenses.HomepageUrls.KTX,
        PYDroidLicenses.LicenseLocations.KTX
    )
    createItem(
        PYDroidLicenses.Names.RXANDROID,
        PYDroidLicenses.HomepageUrls.RXANDROID,
        PYDroidLicenses.LicenseLocations.RXANDROID
    )
    createItem(
        PYDroidUtilLicenses.Names.APPCOMPAT,
        PYDroidUtilLicenses.HomepageUrls.APPCOMPAT,
        PYDroidUtilLicenses.LicenseLocations.APPCOMPAT
    )
    createItem(
        Names.PYDROID,
        HomepageUrls.PYDROID,
        LicenseLocations.PYDROID
    )
    createItem(
        Names.RETROFIT,
        HomepageUrls.RETROFIT,
        LicenseLocations.RETROFIT
    )
    createItem(
        Names.MOSHI,
        HomepageUrls.MOSHI,
        LicenseLocations.MOSHI
    )
    createItem(
        Names.MOSHI_KT_CODEGEN,
        HomepageUrls.MOSHI_KT_CODEGEN,
        LicenseLocations.MOSHI_KT_CODEGEN
    )
    createItem(
        Names.OKHTTP,
        HomepageUrls.OKHTTP,
        LicenseLocations.OKHTTP
    )
    createItem(
        Names.OKIO,
        HomepageUrls.OKIO,
        LicenseLocations.OKIO
    )
  }

  private fun createItem(
    name: String,
    homepageUrl: String,
    licenseLocation: String
  ) {
    val item = AboutLibrariesModel.create(
        name,
        homepageUrl, licenseLocation
    )
    licenses.add(item)
  }

  object Names {
    const val PYDROID = "PYDroid"
    const val RETROFIT = "Retrofit"
    const val MOSHI = "Moshi"
    const val MOSHI_KT_CODEGEN = "Moshi Kotlin Codegen"
    const val OKHTTP = "OkHTTP"
    const val OKIO = "Okio"
  }

  object HomepageUrls {
    const val PYDROID = "https://pyamsoft.github.io/pydroid"
    const val RETROFIT = "https://github.com/square/retrofit"
    const val MOSHI = "https://github.com/square/moshi"
    const val MOSHI_KT_CODEGEN = "https://github.com/square/moshi"
    const val OKHTTP = "https://github.com/square/okhttp"
    const val OKIO = "https://github.com/square/okio"
  }

  object LicenseLocations {
    const val PYDROID = PYDroidLicenses.LicenseLocations.__DIR + "pydroid"
    const val RETROFIT = PYDroidLicenses.LicenseLocations.__DIR + "retrofit"
    const val MOSHI = PYDroidLicenses.LicenseLocations.__DIR + "moshi"
    const val MOSHI_KT_CODEGEN = PYDroidLicenses.LicenseLocations.__DIR + "moshi-kt-codegen"
    const val OKHTTP = PYDroidLicenses.LicenseLocations.__DIR + "okhttp"
    const val OKIO = PYDroidLicenses.LicenseLocations.__DIR + "okio"
  }

  @JvmStatic
  fun create(
    name: String,
    homepageUrl: String,
    licenseLocation: String
  ) {
    createItem(name, homepageUrl, licenseLocation)
  }

  @JvmStatic
  @CheckResult
  fun getLicenses(): Set<AboutLibrariesModel> = Collections.unmodifiableSet(licenses.toSet())
}
