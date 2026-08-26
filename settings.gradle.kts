/*
 * Copyright 2026 pyamsoft
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

pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  repositories {
    mavenLocal()
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

gradle.lifecycle.beforeProject {
  tasks.withType<JavaCompile>().configureEach {
    // More lint warnings surface
    options.compilerArgs.add("-Xlint:unchecked")
    options.compilerArgs.add("-Xlint:deprecation")
    options.isDeprecation = true

    // Fork for faster performance
    // https://docs.gradle.org/current/userguide/performance.html#run_compiler_as_separate_process
    options.isFork = true
  }

  // Optimize tests
  tasks.withType<Test>().configureEach {
    // Run tests in parallel
    // https://docs.gradle.org/current/userguide/performance.html#run_tests_in_parallel
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2

    // Disable report generation, we don't care
    // https://docs.gradle.org/current/userguide/performance.html#disable_test_reports
    reports.html.required.set(false)
    reports.junitXml.required.set(false)

    // More heap for faster tests
    maxHeapSize = "4g"
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" apply true }

rootProject.name = "PYDroid"

include(":arch")

include(":billing")

include(":billing-noop")

include(":billing-play")

include(":bootstrap")

include(":bootstrap-noop")

include(":bootstrap-play")

include(":bus")

include(":core")

include(":notify")

include(":theme")

include(":ui")

include(":util")
