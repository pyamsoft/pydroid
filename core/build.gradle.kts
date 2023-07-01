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

android {
  namespace = "com.pyamsoft.pydroid.core"

  kotlinOptions { freeCompilerArgs += "-Xexplicit-api=strict" }

  defaultConfig {
    // Android Testing
    // https://developer.android.com/training/testing/instrumented-tests
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
}

dependencies {
  // Expose annotations
  api("androidx.annotation:annotation:1.6.0")

  // Expose Coroutines
  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutines"]}")

  // Testing
  testImplementation("org.jetbrains.kotlin:kotlin-test:${rootProject.extra["kotlin"]}")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines"]}")

  androidTestImplementation("androidx.test:runner:${rootProject.extra["testRunner"]}")
  androidTestImplementation("org.jetbrains.kotlin:kotlin-test:${rootProject.extra["kotlin"]}")
  androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines"]}")
}
