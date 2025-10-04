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
  implementation(libs.androidx.lifecycle)
  implementation(libs.androidx.activity)

  // Testing
  testImplementation(libs.kotlin.test)
  testImplementation(libs.kotlinx.coroutines.test)

  androidTestImplementation(libs.androidx.testRunner)
  androidTestImplementation(libs.kotlin.test)
  androidTestImplementation(libs.kotlinx.coroutines.test)

  api(project(":core"))
}
