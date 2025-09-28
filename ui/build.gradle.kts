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

plugins { id("org.jetbrains.kotlin.plugin.compose") }

android {
  namespace = "com.pyamsoft.pydroid.ui"

  buildFeatures { compose = true }
}

dependencies {
  // DataStore
  implementation("androidx.datastore:datastore-preferences:${rootProject.extra["dataStore"]}")

  // Lifecycle support
  implementation("androidx.lifecycle:lifecycle-common:${rootProject.extra["lifecycle"]}")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:${rootProject.extra["lifecycle"]}")

  // Needed just for androidx.preference.PreferenceManager
  // Eventually, big G may push for DataStore being a requirement, which will be pain
  // This pulls in all the UI bits too, which is a little lame.
  implementation("androidx.preference:preference:${rootProject.extra["preference"]}")

  // Compose
  implementation("androidx.compose.ui:ui:${rootProject.extra["compose"]}")
  implementation("androidx.compose.material3:material3:${rootProject.extra["composeMaterial3"]}")
  implementation("androidx.compose.animation:animation:${rootProject.extra["compose"]}")
  implementation(
      "androidx.compose.material:material-icons-core:${rootProject.extra["composeMaterial"]}"
  )
  // implementation("androidx.compose.material:material-icons-extended:${rootProject.extra["composeMaterial"]}")

  // Compose Preview
  compileOnly("androidx.compose.ui:ui-tooling-preview:${rootProject.extra["compose"]}")
  debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose"]}")

  // For LocalActivity in compose
  implementation("androidx.activity:activity-compose:${rootProject.extra["activity"]}")

  // Compose Image loading
  implementation("io.coil-kt.coil3:coil-compose-core:${rootProject.extra["coil"]}")

  implementation("androidx.core:core-ktx:${rootProject.extra["core"]}")

  api(project(":arch"))
  api(project(":billing"))
  api(project(":bootstrap"))
  api(project(":util"))
  api(project(":theme"))
}
