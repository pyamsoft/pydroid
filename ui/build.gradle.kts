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

plugins { alias(libs.plugins.compose.compiler) }

android {
  namespace = "com.pyamsoft.pydroid.ui"

  buildFeatures { compose = true }
}

dependencies {
  // DataStore
  implementation(libs.androidx.dataStore)

  // Lifecycle support
  implementation(libs.androidx.lifecycle)
  implementation(libs.androidx.lifecycle.compose)

  // Needed just for androidx.preference.PreferenceManager
  // Eventually, big G may push for DataStore being a requirement, which will be pain
  // This pulls in all the UI bits too, which is a little lame.
  implementation(libs.androidx.preference)

  // Compose
  implementation(libs.compose.ui)
  implementation(libs.compose.material3)
  implementation(libs.compose.animation)
  implementation(libs.compose.material.icons)
  // implementation(libs.compose.material.icons.extended)

  // Compose Preview
  compileOnly(libs.compose.ui.tooling.preview)
  debugImplementation(libs.compose.ui.tooling)

  // For LocalActivity in compose
  implementation(libs.androidx.activity.compose)

  // Compose Image loading
  implementation(libs.coil.compose)

  implementation(libs.androidx.core.ktx)

  api(project(":arch"))
  api(project(":billing"))
  api(project(":bootstrap"))
  api(project(":util"))
  api(project(":theme"))
}
