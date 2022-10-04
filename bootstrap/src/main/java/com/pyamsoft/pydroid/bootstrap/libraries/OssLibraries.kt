/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.bootstrap.libraries

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.util.contains

/** Manage the various open source libraries */
public object OssLibraries {

  private val libraries = mutableSetOf<OssLibrary>()

  // These libraries are disabled by default and should be enabled at runtime
  /** Using pydroid-arch library */
  public var usingArch: Boolean = false

  /** Using pydroid-autopsy library */
  public var usingAutopsy: Boolean = false

  /** Using pydroid-notify library */
  public var usingNotify: Boolean = false

  /** Using pydroid-ui library */
  public var usingUi: Boolean = false

  /** Using pydroid-theme library */
  public var usingTheme: Boolean = false

  /** Using pydroid-bus library */
  public var usingBus: Boolean = false

  /** Using pydroid-biling library */
  public var usingBilling: Boolean = false

  /** Using pydroid-util library */
  public var usingUtil: Boolean = false

  /** Using pydroid-inject library */
  public var usingInject: Boolean = false

  private var addedBus: Boolean = false
  private var addedBilling: Boolean = false
  private var addedBuild: Boolean = false
  private var addedCore: Boolean = false
  private var addedBootstrap: Boolean = false
  private var addedArch: Boolean = false
  private var addedArchCompose: Boolean = false
  private var addedUiCompose: Boolean = false
  private var addedAutopsy: Boolean = false
  private var addedNotify: Boolean = false
  private var addedUi: Boolean = false
  private var addedTheme: Boolean = false
  private var addedUtil: Boolean = false
  private var addedInject: Boolean = false

  private fun addBuildLibraries() {
    if (addedBuild) {
      return
    }
    addedBuild = true

    add(
        "Gradle Versions Plugin",
        "https://github.com/ben-manes/gradle-versions-plugin",
        "Gradle plugin to discover dependency updates.",
    )
    add(
        "Gradle Spotless Plugin",
        "https://github.com/diffplug/spotless/tree/master/plugin-gradle",
        "Keep your code Spotless with Gradle",
    )
  }

  private fun addCoreLibraries() {
    if (addedCore) {
      return
    }
    addedCore = true

    add(
        "PYDroid Core",
        "https://github.com/pyamsoft/pydroid",
        "The core PYDroid library, providing the building blocks for extension libraries",
    )
    add(
        "Android SDK",
        "https://source.android.com",
        "The Android SDK, which powers everything about the devices we all love.",
    )
    add(
        "Kotlin",
        "https://github.com/JetBrains/kotlin",
        "The Kotlin Programming Language.",
    )

    addBuildLibraries()
  }

  private fun addUtilLibraries() {
    if (addedUtil) {
      return
    }
    addedUtil = true

    add(
        "PYDroid Util",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid util extensions for easier data manipulation",
    )
    add(
        "AndroidX Core",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/core/",
        "The AndroidX Jetpack Core library. Degrade gracefully on older versions of Android.",
    )

    addCoreLibraries()
  }

  private fun addBootstrapLibraries() {
    if (addedBootstrap) {
      return
    }
    addedBootstrap = true

    add(
        "PYDroid Bootstrap",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid bootstrap extensions for quickly spinning up new applications",
    )
    add(
        "Cachify",
        "https://github.com/pyamsoft/cachify",
        "Simple in-memory caching of all the things",
    )

    add(
        "Google Play In-App Updates Library",
        "https://developers.google.com/android/",
        "Google Play Services In-App Updates library for Android.",
        license =
            OssLicenses.custom(
                license = "Custom Google License",
                location = "https://developer.android.com/distribute/play-services",
            ),
    )

    add(
        "Google Play In-App Review Library",
        "https://developers.google.com/android/",
        "Google Play Services In-App Review library for Android.",
        license =
            OssLicenses.custom(
                license = "Custom Google License",
                location = "https://developer.android.com/distribute/play-services",
            ),
    )

    add(
        "AndroidX Core KTX",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/core/ktx/",
        "The AndroidX Jetpack Core KTX library. Write more concise, idiomatic Kotlin code.",
    )

    addUtilLibraries()
  }

  private fun addUiLibraries() {
    if (addedUi) {
      return
    }
    addedUi = true

    add(
        "PYDroid UI",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid reference implementation for various UI components",
    )
    add(
        "AndroidX Core KTX",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/core/ktx/",
        "The AndroidX Jetpack Core KTX library. Write more concise, idiomatic Kotlin code.",
    )
    add(
        "AndroidX Vector Drawable",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/vectordrawable/",
        "The AndroidX Jetpack Vector Drawable Compat library. Create drawables based on XML vector graphics.",
    )
    add(
        "Material Components Android",
        "https://github.com/material-components/material-components-android",
        "Modular and customizable Material Design UI components for Android.",
    )
    add(
        "Coil Compose",
        "https://github.com/coil-kt/Coil",
        "An image loading library for Android backed by Kotlin Coroutines.",
    )

    add(
        "AndroidX Preference",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/preference/",
        "The AndroidX Jetpack Preference library. Allow users to modify UI settings.",
    )
    addComposeUiLibraries()
    addArchLibraries()
    addBillingLibraries()
    addBootstrapLibraries()
    addInjectLibraries()
    addUtilLibraries()
    addThemeLibraries()
  }

  private fun addThemeLibraries() {
    if (addedTheme) {
      return
    }
    addedTheme = true

    add(
        "PYDroid Theme",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid extensions for MaterialTheme",
    )

    addCoreLibraries()
  }

  private fun addArchLibraries() {
    if (addedArch) {
      return
    }
    addedArch = true

    add(
        "PYDroid Arch",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid standard architecture for an MVVM UI design pattern",
    )
    add(
        "Highlander",
        "https://github.com/pyamsoft/highlander",
        "There can be only one. A coroutine powered runner which guarantees that the only one instance of a runner function is active at any given time.",
    )
    add(
        "Kotlin Coroutines",
        "https://github.com/Kotlin/kotlinx.coroutines",
        "Library support for Kotlin coroutines with multiplatform support.",
    )
    add(
        "AndroidX Lifecycle",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/lifecycle/",
        "The AndroidX Jetpack Lifecycle library. Manages your activity and fragment lifecycles.",
    )
    addComposeArchLibraries()
    addBusLibraries()
    addUtilLibraries()
  }

  private fun addComposeArchLibraries() {
    if (addedArchCompose) {
      return
    }
    addedArchCompose = true

    add(
        "Jetpack Compose Compiler",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose",
        "Jetpack Compose is Android’s modern toolkit for building native UI",
    )
    add(
        "Jetpack Compose Runtime",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose",
        "Jetpack Compose is Android’s modern toolkit for building native UI",
    )
  }

  private fun addComposeUiLibraries() {
    if (addedUiCompose) {
      return
    }
    addedUiCompose = true

    add(
        "Jetpack Compose UI",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose",
        "Jetpack Compose is Android’s modern toolkit for building native UI",
    )
    add(
        "Jetpack Compose Animation",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose",
        "Jetpack Compose is Android’s modern toolkit for building native UI",
    )
    add(
        "Jetpack Compose Material",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose",
        "Jetpack Compose is Android’s modern toolkit for building native UI",
    )
    add(
        "Jetpack Compose UI",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose",
        "Jetpack Compose is Android’s modern toolkit for building native UI",
    )

    addComposeArchLibraries()
  }

  private fun addBusLibraries() {
    if (addedBus) {
      return
    }
    addedBus = true

    add(
        "PYDroid Bus",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid event bus",
    )

    addCoreLibraries()
  }

  private fun addBillingLibraries() {
    if (addedBilling) {
      return
    }
    addedBilling = true

    add(
        "PYDroid Billing",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid In-App Billing library",
    )

    add(
        "Google Play In-App Billing Library",
        "https://developers.google.com/android/",
        "In-App Billing management for Android Applications",
        license =
            OssLicenses.custom(
                license = "Custom Google License",
                location = "https://developer.android.com/distribute/play-services",
            ),
    )

    addBusLibraries()
    addUtilLibraries()
  }

  private fun addInjectLibraries() {
    if (addedInject) {
      return
    }
    addedInject = true

    add(
        "PYDroid Inject",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid dependency injection library",
    )

    addCoreLibraries()
  }

  private fun addNotifyLibraries() {
    if (addedNotify) {
      return
    }
    addedNotify = true

    add(
        "PYDroid Notify",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid notification management abstraction library",
    )

    addCoreLibraries()
  }

  private fun addAutopsyLibraries() {
    if (addedAutopsy) {
      return
    }
    addedAutopsy = true

    add(
        "AndroidX Startup",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/startup/",
        "Helps with Application initialization",
    )
    add(
        "PYDroid Autopsy",
        "https://github.com/pyamsoft/pydroid",
        "PYDroid development crash reporting screen",
    )

    addComposeUiLibraries()
    addCoreLibraries()
    addThemeLibraries()
  }

  /** Add a new library to the list of libraries used by the application */
  @JvmStatic
  @JvmOverloads
  public fun add(
      name: String,
      url: String,
      description: String,
      license: LibraryLicense = OssLicenses.APACHE2
  ) {
    val lib =
        OssLibrary(
            name = name,
            description = description,
            libraryUrl = url,
            licenseName = license.license,
            licenseUrl = license.location,
        )

    if (!libraries.contains { it.key == lib.key }) {
      libraries.add(lib)
    }
  }

  /** Get the list of libraries used in the application */
  @JvmStatic
  @CheckResult
  public fun libraries(): Set<OssLibrary> {
    addBootstrapLibraries()

    if (usingUtil) {
      addUtilLibraries()
    }

    if (usingArch) {
      addArchLibraries()
    }

    if (usingAutopsy) {
      addAutopsyLibraries()
    }

    if (usingBilling) {
      addBillingLibraries()
    }

    if (usingBus) {
      addBusLibraries()
    }

    if (usingInject) {
      addInjectLibraries()
    }

    if (usingNotify) {
      addNotifyLibraries()
    }

    if (usingTheme) {
      addThemeLibraries()
    }

    if (usingUi) {
      addUiLibraries()
    }

    return libraries
  }
}
