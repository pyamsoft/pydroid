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

package com.pyamsoft.pydroid.bootstrap.libraries

import androidx.annotation.CheckResult
import java.util.Collections

object OssLibraries {

  private val libraries: MutableSet<OssLibrary> = LinkedHashSet()

  var CORE = true
  var UTIL = true
  var BOOTSTRAP = true

  // These libraries are disabled by default and should be enabled at runtime
  var UI = false
  var LOADER = false

  private fun addCoreLibraries() {
    add("PYDroid", "https://github.com/pyamsoft/pydroid")
    add("Android SDK", "https://source.android.com")
    add("AndroidX Lifecycle", "https://source.android.com")
    add("Dexcount Gradle Plugin", "https://github.com/KeepSafe/dexcount-gradle-plugin")
    add("Gradle Versions Plugin", "https://github.com/ben-manes/gradle-versions-plugin")
    add("Kotlin", "https://github.com/JetBrains/kotlin")
    add("AndroidX KTX", "https://github.com/android/android-ktx")
    add("Repo", "https://github.com/POPinNow/Repo")
    add("RxJava", "https://github.com/ReactiveX/RxJava")
    add("RxAndroid", "https://github.com/ReactiveX/RxAndroid")
    add("Timber", "https://github.com/JakeWharton/timber")
  }

  private fun addUtilLibraries() {
    add("AndroidX AppCompat", "https://source.android.com")
  }

  private fun addBootstrapLibraries() {
    add("AndroidX Preference", "https://source.android.com")
    add("Retrofit", "https://square.github.io/retrofit/")
    add("Moshi", "https://github.com/square/moshi")
    add("OkHTTP", "https://github.com/square/okhttp")
  }

  private fun addUiLibraries() {
    add("AndroidX DataBinding", "https://source.android.com")
    add("AndroidX RecyclerView", "https://source.android.com")
    add("AndroidX Coordinator Layout", "https://source.android.com")
    add("AndroidX Constraint Layout", "https://source.android.com")
    add("AndroidX Vector Drawable", "https://source.android.com")
    add(
        "Material Components Android",
        "https://github.com/material-components/material-components-android"
    )
  }

  private fun addLoaderLibraries() {
    add("Glide", "https://github.com/bumptech/glide")
  }

  @JvmOverloads
  @JvmStatic
  fun add(
    name: String,
    url: String,
    description: String = "",
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
    if (CORE) {
      addCoreLibraries()
    }
    if (UTIL) {
      addUtilLibraries()
    }
    if (BOOTSTRAP) {
      addBootstrapLibraries()
    }
    if (UI) {
      addUiLibraries()
    }
    if (LOADER) {
      addLoaderLibraries()
    }
    return Collections.unmodifiableSet(libraries)
  }

}
