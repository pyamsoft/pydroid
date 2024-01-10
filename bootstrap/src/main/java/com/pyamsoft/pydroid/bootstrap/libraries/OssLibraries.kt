/*
 * Copyright 2023 pyamsoft
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

  /** Using pydroid-billing library */
  public var usingBilling: Boolean = false

  /** Using pydroid-util library */
  public var usingUtil: Boolean = false

  private var addedBus: Boolean = false
  private var addedBilling: Boolean = false
  private var addedBuild: Boolean = false
  private var addedCore: Boolean = false
  private var addedBootstrap: Boolean = false
  private var addedArch: Boolean = false
  private var addedAutopsy: Boolean = false
  private var addedNotify: Boolean = false
  private var addedUi: Boolean = false
  private var addedTheme: Boolean = false
  private var addedUtil: Boolean = false

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
    add(
        "Dokka",
        "https://github.com/Kotlin/dokka",
        "API documentation engine for Kotlin",
    )
    add(
        "Binary Compatibility Validator",
        "https://github.com/Kotlin/binary-compatibility-validator",
        "Public API management tool",
    )
    add(
        "Android Cache Fix Gradle Plugin",
        "https://github.com/gradle/android-cache-fix-gradle-plugin",
        "Gradle plugin that fixes Android build caching problems.",
    )
    add(
        "Gradle Doctor",
        "https://runningcode.github.io/gradle-doctor/",
        "The right prescription for your Gradle build.",
    )
    add(
        "Core Library Desugaring",
        "https://github.com/google/desugar_jdk_libs",
        "This project contains a small subset of OpenJDK libraries simplified for use on older runtimes.",
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
        "Kotlin",
        "https://github.com/JetBrains/kotlin",
        "The Kotlin Programming Language.",
    )

    add(
        "Kotlin Coroutines",
        "https://github.com/Kotlin/kotlinx.coroutines",
        "Library support for Kotlin coroutines with multiplatform support.",
    )

    add(
        "Android SDK",
        "https://source.android.com",
        "The Android SDK, which powers everything about the devices we all love.",
    )

    add(
        "AndroidX Annotations",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/annotation/",
        "Annotation library offers a set of Java annotations that are useful for Android application and library development.",
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
        "AndroidX Activity",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/activity/",
        "Activity library offers a ComponentActivity which is a base class for activities used in androidx that enables composition of higher level components.",
    )

    add(
        "AndroidX Lifecycle Common",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/lifecycle/lifecycle-common",
        "Lifecycle library provides support for Android component lifecycle.",
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
        "Jetpack Compose Runtime",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/runtime/runtime",
        "Jetpack Compose runtime annotations",
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
        "Coil Compose",
        "https://github.com/coil-kt/Coil",
        "An image loading library for Android backed by Kotlin Coroutines.",
    )

    add(
        "AndroidX Preference",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/preference/",
        "The AndroidX Jetpack Preference library. Allow users to modify UI settings.",
    )

    add(
        "AndroidX Core KTX",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/core/ktx/",
        "The AndroidX Jetpack Core KTX library. Write more concise, idiomatic Kotlin code.",
    )

    add(
        "AndroidX Lifecycle Common",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/lifecycle/lifecycle-common",
        "Lifecycle library provides support for Android component lifecycle.",
    )

    add(
        "AndroidX Lifecycle Compose",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/lifecycle/lifecycle-runtime-compose",
        "Lifecycle library provides support for Android component lifecycle, with Compose runtime support",
    )

    add(
        "Jetpack Compose UI",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/ui/ui",
        "Jetpack Compose support for UI widgets",
    )
    add(
        "Jetpack Compose Animation",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/animation",
        "Jetpack Compose support for animations",
    )
    add(
        "Jetpack Compose Material",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/material",
        "Jetpack Compose support for the Material Design system",
    )

    add(
        "Jetpack Compose UI Tooling",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/ui/ui-tooling",
        "Jetpack Compose support buildtime UI tooling",
    )

    addArchLibraries()
    addBillingLibraries()
    addBootstrapLibraries()
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

    add(
        "Jetpack Compose UI",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/ui/ui",
        "Jetpack Compose support for UI widgets",
    )
    add(
        "Jetpack Compose Material",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/material",
        "Jetpack Compose support for the Material Design system",
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
        "Jetpack Compose Runtime Saveable",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/runtime/runtime-saveable",
        "Jetpack Compose runtime support for Saveable state",
    )

    addBusLibraries()
    addUtilLibraries()
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
        "AndroidX Activity",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/activity/",
        "Activity library offers a ComponentActivity which is a base class for activities used in androidx that enables composition of higher level components.",
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

    add(
        "Jetpack Compose Runtime",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/runtime/runtime",
        "Jetpack Compose runtime annotations",
    )

    addBusLibraries()
    addUtilLibraries()
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

    add(
        "AndroidX Core",
        "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/core/",
        "AndroidX Core Libraries",
    )

    addCoreLibraries()
    addUtilLibraries()
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

    add(
        "Jetpack Compose UI",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/ui/ui",
        "Jetpack Compose support for UI widgets",
    )
    add(
        "Jetpack Compose Material",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/material",
        "Jetpack Compose support for the Material Design system",
    )

    add(
        "Jetpack Compose UI Tooling",
        "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/compose/ui/ui-tooling",
        "Jetpack Compose support buildtime UI tooling",
    )

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
    // Since we are in the bootstrap module, this always happens
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
