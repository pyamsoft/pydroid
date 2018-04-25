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

package com.pyamsoft.pydroid

object Licenses {

  object Names {
    const val ANDROID = "Android"
    const val ANDROID_SUPPORT = "Android Support Libraries"
    const val DEXCOUNT_GRADLE_PLUGIN = "Dexcount Gradle Plugin"
    const val GRADLE_VERSIONS_PLUGIN = "Gradle Versions Plugin"
    const val KOTLIN = "Kotlin"
    const val KTX = "Android KTX"
    const val RXANDROID = "RxAndroid"
    const val RXJAVA = "RxJava"
    const val TIMBER = "Timber"
  }

  object HomepageUrls {
    const val ANDROID = "https://source.android.com"
    const val ANDROID_SUPPORT = "https://source.android.com"
    const val DEXCOUNT_GRADLE_PLUGIN = "https://github.com/KeepSafe/dexcount-gradle-plugin"
    const val GRADLE_VERSIONS_PLUGIN = "https://github.com/ben-manes/gradle-versions-plugin"
    const val KOTLIN = "https://github.com/JetBrains/kotlin"
    const val KTX = "https://github.com/android/android-ktx"
    const val RXJAVA = "https://github.com/ReactiveX/RxJava"
    const val RXANDROID = "https://github.com/ReactiveX/RxAndroid"
    const val TIMBER = "https://github.com/JakeWharton/timber"
  }

  object LicenseLocations {

    // Add an underscore to keep this name on top
    const val __DIR = "licenses/"
    const val ANDROID = __DIR + "android"
    const val ANDROID_SUPPORT = __DIR + "androidsupport"
    const val DEXCOUNT_GRADLE_PLUGIN = __DIR + "dexcount-gradle-plugin"
    const val GRADLE_VERSIONS_PLUGIN = __DIR + "gradle-versions-plugin"
    const val KOTLIN = __DIR + "kotlin"
    const val KTX = __DIR + "ktx"
    const val RXANDROID = __DIR + "rxandroid"
    const val RXJAVA = __DIR + "rxjava"
    const val TIMBER = __DIR + "timber"
  }
}
