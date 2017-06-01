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

package com.pyamsoft.pydroid.about

import android.support.annotation.CheckResult
import java.util.ArrayList

class Licenses private constructor() {

  private val licenses: MutableList<AboutLibrariesModel>

  init {
    licenses = ArrayList<AboutLibrariesModel>()
    addCommonLicenses()
  }

  /**
   * These libraries are directly used by PYDroid, and are thus in every application that uses
   * pydroid
   */
  private fun addCommonLicenses() {
    createItem(Names.ANDROID, HomepageUrls.ANDROID, LicenseLocations.ANDROID)
    createItem(Names.ANDROID_SUPPORT, HomepageUrls.ANDROID_SUPPORT,
        LicenseLocations.ANDROID_SUPPORT)
    createItem(Names.PYDROID, HomepageUrls.PYDROID, LicenseLocations.PYDROID)
    createItem(Names.AUTO_VALUE, HomepageUrls.AUTO_VALUE, LicenseLocations.AUTO_VALUE)
    createItem(Names.RETROFIT, HomepageUrls.RETROFIT, LicenseLocations.RETROFIT)
    createItem(Names.ERROR_PRONE, HomepageUrls.ERROR_PRONE, LicenseLocations.ERROR_PRONE)
    createItem(Names.TIMBER, HomepageUrls.TIMBER, LicenseLocations.TIMBER)
    createItem(Names.GRADLE_VERSIONS_PLUGIN, HomepageUrls.GRADLE_VERSIONS_PLUGIN,
        LicenseLocations.GRADLE_VERSIONS_PLUGIN)
    createItem(Names.DEXCOUNT_GRADLE_PLUGIN, HomepageUrls.DEXCOUNT_GRADLE_PLUGIN,
        LicenseLocations.DEXCOUNT_GRADLE_PLUGIN)
    createItem(Names.RXJAVA, HomepageUrls.RXJAVA, LicenseLocations.RXJAVA)
    createItem(Names.RXANDROID, HomepageUrls.RXANDROID, LicenseLocations.RXANDROID)
  }

  private fun createItem(name: String, homepageUrl: String, licenseLocation: String) {
    val item = AboutLibrariesModel.create(name, homepageUrl, licenseLocation)
    licenses.add(item)
  }

  private fun createItemWithContent(name: String, homepageUrl: String, content: String) {
    val item = AboutLibrariesModel.createWithContent(name, homepageUrl, content)
    licenses.add(item)
  }

  private class Names private constructor() {

    init {
      throw RuntimeException("No instances")
    }

    companion object {

      internal val RXJAVA = "RxJava"
      internal val RXANDROID = "RxAndroid"
      internal val ANDROID = "Android"
      internal val ANDROID_SUPPORT = "Android Support Libraries"
      internal val PYDROID = "PYDroid"
      internal val AUTO_VALUE = "AutoValue"
      internal val RETROFIT = "Retrofit"
      internal val ERROR_PRONE = "Error Prone"
      internal val TIMBER = "Timber"
      internal val DEXCOUNT_GRADLE_PLUGIN = "Dexcount Gradle Plugin"
      internal val GRADLE_VERSIONS_PLUGIN = "Gradle Versions Plugin"
    }
  }

  private class HomepageUrls private constructor() {

    init {
      throw RuntimeException("No instances")
    }

    companion object {
      internal val RXJAVA = "https://github.com/ReactiveX/RxJava"
      internal val RXANDROID = "https://github.com/ReactiveX/RxAndroid"
      internal val ANDROID = "https://source.android.com"
      internal val ANDROID_SUPPORT = "https://source.android.com"
      internal val PYDROID = "https://pyamsoft.github.io/pydroid"
      internal val AUTO_VALUE = "https://github.com/google/auto"
      internal val RETROFIT = "https://square.github.io/retrofit/"
      internal val ERROR_PRONE = "https://github.com/google/error-prone"
      internal val TIMBER = "https://github.com/JakeWharton/timber"
      internal val DEXCOUNT_GRADLE_PLUGIN = "https://github.com/KeepSafe/dexcount-gradle-plugin"
      internal val GRADLE_VERSIONS_PLUGIN = "https://github.com/ben-manes/gradle-versions-plugin"
    }
  }

  class LicenseLocations private constructor() {

    init {
      throw RuntimeException("No instances")
    }

    companion object {
      // Add an underscore to keep this name on top
      @JvmField val _BASE = "licenses/"
      internal val RXJAVA = _BASE + "rxjava"
      internal val RXANDROID = _BASE + "rxandroid"
      internal val ANDROID_SUPPORT = _BASE + "androidsupport"
      internal val ANDROID = _BASE + "android"
      internal val PYDROID = _BASE + "pydroid"
      internal val AUTO_VALUE = _BASE + "autovalue"
      internal val RETROFIT = _BASE + "retrofit"
      internal val ERROR_PRONE = _BASE + "errorprone"
      internal val TIMBER = _BASE + "timber"
      internal val DEXCOUNT_GRADLE_PLUGIN = _BASE + "dexcount-gradle-plugin"
      internal val GRADLE_VERSIONS_PLUGIN = _BASE + "gradle-versions-plugin"
    }
  }

  companion object {

    @JvmStatic private val INSTANCE = Licenses()

    @JvmStatic fun create(name: String, homepageUrl: String, licenseLocation: String) {
      INSTANCE.createItem(name, homepageUrl, licenseLocation)
    }

    @JvmStatic fun createWithContent(name: String, homepageUrl: String, content: String) {
      INSTANCE.createItemWithContent(name, homepageUrl, content)
    }

    @JvmStatic @CheckResult fun getLicenses(): List<AboutLibrariesModel> {
      return INSTANCE.licenses.toList()
    }
  }
}
