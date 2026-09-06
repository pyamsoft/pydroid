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

import com.android.build.api.dsl.LibraryExtension
import com.deezer.caupain.plugin.DependenciesUpdateTask
import com.deezer.caupain.policies.StabilityLevelPolicy
import dev.detekt.gradle.extensions.FailOnSeverity
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
  // Android AGP
  // https://developer.android.com/studio/build#top-level
  alias(libs.plugins.android) apply false

  // Fix Android build cache
  // https://github.com/gradle/android-cache-fix-gradle-plugin
  alias(libs.plugins.android.cacheFix) apply true

  // Jetpack Compose Compiler
  // https://android-developers.googleblog.com/2024/04/jetpack-compose-compiler-moving-to-kotlin-repository.html
  alias(libs.plugins.compose.compiler) apply false

  // Dokka
  // https://github.com/Kotlin/dokka
  alias(libs.plugins.dokka) apply true

  // Spotless
  // https://github.com/diffplug/spotless
  alias(libs.plugins.spotless) apply true

  // Caupain
  // https://github.com/deezer/caupain/blob/main/gradle-plugin/README.md
  alias(libs.plugins.caupain) apply true

  // Detekt
  // https://detekt.dev/docs/gettingstarted/gradle
  alias(libs.plugins.detekt) apply true
}

subprojects {
  apply(plugin = rootProject.libs.plugins.android.asProvider().get().pluginId)
  apply(plugin = "maven-publish")

  extensions.configure<LibraryExtension> {
    compileSdk = rootProject.libs.versions.compileSdk.get().toInt()

    defaultConfig {
      minSdk = rootProject.libs.versions.minSdk.get().toInt()

      // Consumer proguard file
      consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_21
      targetCompatibility = JavaVersion.VERSION_21

      // Flag to enable support for the new language APIs
      isCoreLibraryDesugaringEnabled = true
    }

    publishing {
      singleVariant("release") {
        withSourcesJar()
        withJavadocJar()
      }
    }

    buildTypes {
      debug {
        isMinifyEnabled = false
        isShrinkResources = false
      }

      release {
        isMinifyEnabled = false
        isShrinkResources = false
      }
    }

    buildFeatures {
      buildConfig = false
    }
  }

  extensions.configure<KotlinAndroidProjectExtension> {
    compilerOptions {
      languageVersion = KotlinVersion.KOTLIN_2_4
      jvmTarget = JvmTarget.JVM_21
      freeCompilerArgs.add("-Xexplicit-api=strict")
    }
  }

  afterEvaluate {
    extensions.configure<PublishingExtension> {
      publications {
        create<MavenPublication>("PYDroid") {
          from(project.components["release"])

          artifactId = project.name
          groupId = "com.github.pyamsoft.pydroid"
          version = "30.0.0"
        }
      }
    }
  }

  dependencies {
    add("coreLibraryDesugaring", rootProject.libs.android.desugar)
  }
}

detekt {
  debug = true
  buildUponDefaultConfig = true
  parallel = true
  failOnSeverity = FailOnSeverity.Warning
  config.setFrom(projectDir.absolutePath + "/tools/detekt/config.yml")
}

spotless {
  java {
    target("**/*.java")

    removeUnusedImports()
    trimTrailingWhitespace()
    endWithNewline()
    leadingTabsToSpaces(2)
  }
  kotlin {
    target("**/*.kt")
    ktfmt(libs.versions.ktfmt.get())

    trimTrailingWhitespace()
    endWithNewline()
    leadingTabsToSpaces(2)
  }
  kotlinGradle {
    target("*.gradle.kts")
    ktfmt(libs.versions.ktfmt.get())

    trimTrailingWhitespace()
    endWithNewline()
    leadingTabsToSpaces(2)
  }
}

// Caupain Version Strategy
tasks.withType<DependenciesUpdateTask>().configureEach {
  // Pick only "stable" versions
  selectIf(StabilityLevelPolicy)
}
