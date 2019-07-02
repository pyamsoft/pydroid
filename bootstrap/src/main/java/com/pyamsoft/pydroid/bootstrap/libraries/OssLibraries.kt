/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.bootstrap.libraries

import androidx.annotation.CheckResult
import java.util.Collections

object OssLibraries {

  private val libraries: MutableSet<OssLibrary> = LinkedHashSet()

  var UTIL = true
  var BOOTSTRAP = true

  // These libraries are disabled by default and should be enabled at runtime
  var ARCH = false
  var UI = false
  var LOADER = false

  private fun addBuildLibraries() {
    add(
        "Dexcount Gradle Plugin",
        "https://github.com/KeepSafe/dexcount-gradle-plugin",
        "A Gradle plugin to report the number of method references in your APK on every build."
    )
    add(
        "Gradle Versions Plugin",
        "https://github.com/ben-manes/gradle-versions-plugin",
        "Gradle plugin to discover dependency updates."
    )
  }

  private fun addCoreLibraries() {
    add(
        "PYDroid Core",
        "https://github.com/pyamsoft/pydroid",
        "The core PYDroid library, providing the building blocks for extension libraries"
    )
    add(
        "Android SDK",
        "https://source.android.com",
        "The Android SDK, which powers everything about the devices we all love."
    )
    add(
        "AndroidX Lifecycle",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/lifecycle/",
        "The AndroidX Jetpack Lifecycle library. Manages your activity and fragment lifecycles."
    )
    add(
        "Kotlin",
        "https://github.com/JetBrains/kotlin",
        "The Kotlin Programming Language."
    )
    add(
        "Timber",
        "https://github.com/JakeWharton/timber",
        "A logger with a small, extensible API which provides utility on top of Android's normal Log class."
    )
  }

  private fun addUtilLibraries() {
    add(
        "PYDroid Util",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid util extensions for easier data manipulation"
    )
    add(
        "AndroidX Core",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/core/",
        "The AndroidX Jetpack Core library. Degrade gracefully on older versions of Android."
    )
  }

  private fun addBootstrapLibraries() {
    add(
        "PYDroid Bootstrap",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid bootstrap extensions for quickly spinning up new applications"
    )
    add(
        "Retrofit",
        "https://square.github.io/retrofit/",
        "Type-safe HTTP client for Android and Java by Square, Inc."
    )
    add(
        "Moshi",
        "https://github.com/square/moshi",
        "A modern JSON library for Android and Java."
    )
    add(
        "OkHTTP",
        "https://github.com/square/okhttp",
        "An HTTP+HTTP/2 client for Android and Java applications."
    )
    add(
        "Cachify",
        "https://github.com/pyamsoft/cachify",
        "Simple in-memory caching of all the things"
    )
  }

  private fun addUiLibraries() {
    add(
        "PYDroid UI",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid reference implementation for various UI components"
    )
    add(
        "AndroidX Core KTX",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/core/ktx/",
        "The AndroidX Jetpack Core KTX library. Write more concise, idiomatic Kotlin code."
    )
    add(
        "AndroidX RecyclerView",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/recyclerview/",
        "The AndroidX Jetpack RecyclerView library. Create efficient list views."
    )
    add(
        "AndroidX Vector Drawable",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/graphics/drawable/static/",
        "The AndroidX Jetpack Vector Drawable Compat library. Create drawables based on XML vector graphics."
    )
    add(
        "Material Components Android",
        "https://github.com/material-components/material-components-android",
        "Modular and customizable Material Design UI components for Android."
    )
    add(
        "AndroidX Preference",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/preference/",
        "The AndroidX Jetpack Preference library. Allow users to modify UI settings."
    )
    add(
        "AndroidX Constraint Layout",
        "https://android.googlesource.com/platform/frameworks/opt/sherpa/+/studio-master-dev/constraintlayout/",
        "The AndroidX Jetpack Constraint Layout library. Position and size widgets in a flexible way."
    )
  }

  private fun addLoaderLibraries() {
    add(
        "PYDroid Loader",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid image loader abstraction library"
    )
    add(
        "Glide",
        "https://github.com/bumptech/glide",
        "An image loading and caching library for Android focused on smooth scrolling."
    )
  }

  private fun addArchLibraries() {
    add(
        "PYDroid Arch",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid standard architecture for a UiComponent based, ViewModel driven, reactive MVI UI design pattern"
    )
    add(
        "Highlander",
        "https://github.com/pyamsoft/highlander",
        "There can only be one. A coroutine powered runner which guarantees that the only one instance of a runner function is active at any given time."
    )
    add(
        "Kotlin Coroutines",
        "https://github.com/Kotlin/kotlinx.coroutines",
        "Library support for Kotlin coroutines with multiplatform support."
    )
    add(
        "AndroidX Lifecycle ViewModel KTX",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/lifecycle/",
        "Kotlin extensions for the Android Jetpack ViewModel"
    )
    add(
        "AndroidX Lifecycle ViewModel",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/lifecycle/",
        "The AndroidX Jetpack ViewModel library. Model the state of your application easily."
    )
  }

  @JvmOverloads
  @JvmStatic
  fun add(
    name: String,
    url: String,
    description: String,
    license: OssLicenses = OssLicenses.APACHE2
  ) {
    libraries.add(
        OssLibrary(
            name = name,
            description = description,
            libraryUrl = url,
            licenseName = license.license,
            licenseUrl = license.location
        )
    )
  }

  @JvmStatic
  @CheckResult
  fun libraries(): Set<OssLibrary> {
    // Core and Build is always added if you're using any PYDroid
    addCoreLibraries()
    addBuildLibraries()

    if (UTIL) {
      addUtilLibraries()
    }
    if (BOOTSTRAP) {
      addBootstrapLibraries()
    }
    if (UI) {
      addUiLibraries()
    }
    if (ARCH) {
      addArchLibraries()
    }
    if (LOADER) {
      addLoaderLibraries()
    }
    return Collections.unmodifiableSet(libraries)
  }

}
