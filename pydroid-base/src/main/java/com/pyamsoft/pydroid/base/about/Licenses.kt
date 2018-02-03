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

package com.pyamsoft.pydroid.base.about

import android.support.annotation.CheckResult
import java.util.Collections

object Licenses {

  private val licenses: MutableList<AboutLibrariesModel>

  init {
    licenses = ArrayList()
    addCommonLicenses()
  }

  /**
   * These libraries are directly used by PYDroid, and are thus in every application that uses
   * pydroid
   */
  private fun addCommonLicenses() {
    createItem(
        Names.ANDROID,
        HomepageUrls.ANDROID,
        LicenseLocations.ANDROID
    )
    createItem(
        Names.ANDROID_SUPPORT,
        HomepageUrls.ANDROID_SUPPORT,
        LicenseLocations.ANDROID_SUPPORT
    )
    createItem(
        Names.PYDROID,
        HomepageUrls.PYDROID,
        LicenseLocations.PYDROID
    )
    createItem(
        Names.AUTO_VALUE,
        HomepageUrls.AUTO_VALUE,
        LicenseLocations.AUTO_VALUE
    )
    createItem(
        Names.RETROFIT,
        HomepageUrls.RETROFIT,
        LicenseLocations.RETROFIT
    )
    createItem(
        Names.TIMBER,
        HomepageUrls.TIMBER,
        LicenseLocations.TIMBER
    )
    createItem(
        Names.GRADLE_VERSIONS_PLUGIN,
        HomepageUrls.GRADLE_VERSIONS_PLUGIN,
        LicenseLocations.GRADLE_VERSIONS_PLUGIN
    )
    createItem(
        Names.DEXCOUNT_GRADLE_PLUGIN,
        HomepageUrls.DEXCOUNT_GRADLE_PLUGIN,
        LicenseLocations.DEXCOUNT_GRADLE_PLUGIN
    )
    createItem(
        Names.RXJAVA,
        HomepageUrls.RXJAVA,
        LicenseLocations.RXJAVA
    )
    createItem(
        Names.RXANDROID,
        HomepageUrls.RXANDROID,
        LicenseLocations.RXANDROID
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

  private object Names {
    internal const val RXJAVA = "RxJava"
    internal const val RXANDROID = "RxAndroid"
    internal const val ANDROID = "Android"
    internal const val ANDROID_SUPPORT = "Android Support Libraries"
    internal const val PYDROID = "PYDroid"
    internal const val AUTO_VALUE = "AutoValue"
    internal const val RETROFIT = "Retrofit"
    internal const val TIMBER = "Timber"
    internal const val DEXCOUNT_GRADLE_PLUGIN = "Dexcount Gradle Plugin"
    internal const val GRADLE_VERSIONS_PLUGIN = "Gradle Versions Plugin"
  }

  private object HomepageUrls {
    internal const val RXJAVA = "https://github.com/ReactiveX/RxJava"
    internal const val RXANDROID = "https://github.com/ReactiveX/RxAndroid"
    internal const val ANDROID = "https://source.android.com"
    internal const val ANDROID_SUPPORT = "https://source.android.com"
    internal const val PYDROID = "https://pyamsoft.github.io/pydroid"
    internal const val AUTO_VALUE = "https://github.com/google/auto"
    internal const val RETROFIT = "https://square.github.io/retrofit"
    internal const val TIMBER = "https://github.com/JakeWharton/timber"
    internal const val DEXCOUNT_GRADLE_PLUGIN = "https://github.com/KeepSafe/dexcount-gradle-plugin"
    internal const val GRADLE_VERSIONS_PLUGIN =
      "https://github.com/ben-manes/gradle-versions-plugin"
  }

  object LicenseLocations {

    // Add an underscore to keep this name on top
    const val _BASE = "licenses/"
    internal const val RXJAVA = _BASE + "rxjava"
    internal const val RXANDROID = _BASE + "rxandroid"
    internal const val ANDROID_SUPPORT = _BASE + "androidsupport"
    internal const val ANDROID = _BASE + "android"
    internal const val PYDROID = _BASE + "pydroid"
    internal const val AUTO_VALUE = _BASE + "autovalue"
    internal const val RETROFIT = _BASE + "retrofit"
    internal const val TIMBER = _BASE + "timber"
    internal const val DEXCOUNT_GRADLE_PLUGIN = _BASE + "dexcount-gradle-plugin"
    internal const val GRADLE_VERSIONS_PLUGIN = _BASE + "gradle-versions-plugin"
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
  fun getLicenses(): List<AboutLibrariesModel> = Collections.unmodifiableList(licenses.toList())
}
