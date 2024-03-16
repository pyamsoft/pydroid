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

android {
  namespace = "com.pyamsoft.pydroid.billing"

  kotlinOptions { freeCompilerArgs += "-Xexplicit-api=strict" }
}

dependencies {
  implementation("androidx.activity:activity:${rootProject.extra["activity"]}")
  implementation("com.android.billingclient:billing:6.2.0")

  // Compose Annotations
  implementation("androidx.compose.runtime:runtime:${rootProject.extra["compose"]}")

  api(project(":bus"))
  api(project(":util"))
}
