/*
 * Copyright 2024 pyamsoft
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
  namespace = "com.pyamsoft.pydroid.theme"

  kotlinOptions { freeCompilerArgs += "-Xexplicit-api=strict" }

  buildFeatures { compose = true }
}

dependencies {
  // For LocalActivity in compose
  implementation("androidx.activity:activity-compose:${rootProject.extra["activity"]}")

  // Compose
  implementation("androidx.compose.ui:ui:${rootProject.extra["compose"]}")
  implementation("androidx.compose.material3:material3:${rootProject.extra["composeMaterial3"]}")

  api(project(":core"))
}
