/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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
  namespace = "com.pyamsoft.pydroid.util"

  defaultConfig {
    // Android Testing
    // https://developer.android.com/training/testing/instrumented-tests
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
}

dependencies {
  implementation("androidx.lifecycle:lifecycle-common:${rootProject.extra["lifecycle"]}")
  implementation("androidx.activity:activity:${rootProject.extra["activity"]}")

  // Needed to silence warning about using ActivityResult APIs
  //
  // In practice, this is most likely pulled in by AndroidX Activity or others
  // but we do it here just to be explicit.
  implementation("androidx.fragment:fragment:${rootProject.extra["fragment"]}")

  // Testing
  testImplementation("org.jetbrains.kotlin:kotlin-test:${rootProject.extra["kotlin"]}")
  testImplementation(
      "org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines"]}")

  androidTestImplementation("androidx.test:runner:${rootProject.extra["testRunner"]}")
  androidTestImplementation("org.jetbrains.kotlin:kotlin-test:${rootProject.extra["kotlin"]}")
  androidTestImplementation(
      "org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutines"]}")

  api(project(":core"))
}
