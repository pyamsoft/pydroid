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

package com.pyamsoft.pydroid.bootstrap.libraries

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.R
import com.pyamsoft.pydroid.util.contains

/** Manage the various open source libraries */
public object OssLibraries {

  private val libraries = mutableSetOf<OssLibrary>()

  // These libraries are disabled by default and should be enabled at runtime
  /** Using pydroid-arch library */
  public var usingArch: Boolean = false

  /** Using pydroid-autopsy library */
  public var usingAutopsy: Boolean = false

  /** Using pydroid-notify library */
  public var usingNotify: Boolean = false

  /** Using pydroid-ui library */
  public var usingUi: Boolean = false

  /** Using pydroid-theme library */
  public var usingTheme: Boolean = false

  /** Using pydroid-bus library */
  public var usingBus: Boolean = false

  /** Using pydroid-billing library */
  public var usingBilling: Boolean = false

  /** Using pydroid-util library */
  public var usingUtil: Boolean = false

  private var addedBus: Boolean = false
  private var addedBilling: Boolean = false
  private var addedBuild: Boolean = false
  private var addedCore: Boolean = false
  private var addedBootstrap: Boolean = false
  private var addedArch: Boolean = false
  private var addedAutopsy: Boolean = false
  private var addedNotify: Boolean = false
  private var addedUi: Boolean = false
  private var addedTheme: Boolean = false
  private var addedUtil: Boolean = false

  private fun addBuildLibraries(context: Context) {
    if (addedBuild) {
      return
    }
    addedBuild = true

    add(
        context.getString(R.string.gradle_versions_plugin_name),
        context.getString(R.string.gradle_versions_plugin_url),
        context.getString(R.string.gradle_versions_plugin_description),
    )
    add(
        context.getString(R.string.gradle_spotless_plugin),
        context.getString(R.string.gradle_spotless_plugin_url),
        context.getString(R.string.gradle_spotless_plugin_description),
    )
    add(
        context.getString(R.string.dokka),
        context.getString(R.string.dokka_url),
        context.getString(R.string.dokka_description),
    )
    add(
        context.getString(R.string.binary_compatibility_validator),
        context.getString(R.string.binary_compatibility_validator_url),
        context.getString(R.string.binary_compatibility_validator_description),
    )
    add(
        context.getString(R.string.android_cache_fix_plugin),
        context.getString(R.string.android_cache_fix_plugin_url),
        context.getString(R.string.android_cache_fix_plugin_description),
    )
    add(
        context.getString(R.string.gradle_doctor),
        context.getString(R.string.gradle_doctor_url),
        context.getString(R.string.gradle_doctor_description),
    )
    add(
        context.getString(R.string.core_library_desugaring),
        context.getString(R.string.core_library_desugaring_url),
        context.getString(R.string.core_library_desugaring_description),
    )
  }

  private fun addCoreLibraries(context: Context) {
    if (addedCore) {
      return
    }
    addedCore = true

    add(
        context.getString(R.string.pydroid_core),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_core_description),
    )

    add(
        context.getString(R.string.kotlin),
        context.getString(R.string.kotlin_url),
        context.getString(R.string.kotlin_description),
    )

    add(
        context.getString(R.string.kotlin_coroutines),
        context.getString(R.string.kotlin_coroutines_url),
        context.getString(R.string.kotlin_coroutines_description),
    )

    add(
        context.getString(R.string.android_sdk),
        context.getString(R.string.android_sdk_url),
        context.getString(R.string.android_sdk_description),
    )

    add(
        context.getString(R.string.androidx_annotations),
        context.getString(R.string.androidx_annotations_url),
        context.getString(R.string.androidx_annotations_description),
    )

    addBuildLibraries(context)
  }

  private fun addUtilLibraries(context: Context) {
    if (addedUtil) {
      return
    }
    addedUtil = true

    add(
        context.getString(R.string.pydroid_util),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_util_description),
    )

    add(
        context.getString(R.string.androidx_activity),
        context.getString(R.string.androidx_activity_url),
        context.getString(R.string.androidx_activity_description),
    )

    add(
        context.getString(R.string.androidx_fragment),
        context.getString(R.string.androidx_fragment_url),
        context.getString(R.string.androidx_fragment_description),
    )

    add(
        context.getString(R.string.androidx_lifecycle_common),
        context.getString(R.string.androidx_lifecycle_common_url),
        context.getString(R.string.androidx_lifecycle_common_description),
    )

    addCoreLibraries(context)
  }

  private fun addBootstrapLibraries(context: Context) {
    if (addedBootstrap) {
      return
    }
    addedBootstrap = true

    add(
        context.getString(R.string.pydroid_bootstrap),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_bootstrap_description),
    )

    add(
        context.getString(R.string.google_play_in_app_updates_library),
        context.getString(R.string.google_play_url),
        context.getString(R.string.google_play_in_app_updates_library_description),
        license =
            OssLicenses.custom(
                license = context.getString(R.string.custom_google_license),
                location = context.getString(R.string.google_play_url),
            ),
    )

    add(
        context.getString(R.string.google_play_in_app_review_library),
        context.getString(R.string.google_play_url),
        context.getString(R.string.google_play_in_app_review_library_description),
        license =
            OssLicenses.custom(
                license = context.getString(R.string.custom_google_license),
                location = context.getString(R.string.google_play_url),
            ),
    )

    add(
        context.getString(R.string.compose_runtime),
        context.getString(R.string.compose_runtime_url),
        context.getString(R.string.compose_runtime_description),
    )

    addUtilLibraries(context)
  }

  private fun addUiLibraries(context: Context) {
    if (addedUi) {
      return
    }
    addedUi = true

    add(
        context.getString(R.string.pydroid_ui),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_ui_description),
    )

    add(
        context.getString(R.string.coil_compose),
        context.getString(R.string.coil_compose_url),
        context.getString(R.string.coil_compose_description),
    )

    add(
        context.getString(R.string.androidx_preference),
        context.getString(R.string.androidx_preference_url),
        context.getString(R.string.androidx_preference_description),
    )

    add(
        context.getString(R.string.androidx_core_ktx),
        context.getString(R.string.androidx_core_ktx_url),
        context.getString(R.string.androidx_core_ktx_description),
    )

    add(
        context.getString(R.string.androidx_lifecycle_common),
        context.getString(R.string.androidx_lifecycle_common_url),
        context.getString(R.string.androidx_lifecycle_common_description),
    )

    add(
        context.getString(R.string.androidx_lifecycle_compose),
        context.getString(R.string.androidx_lifecycle_compose_url),
        context.getString(R.string.androidx_lifecycle_compose_description),
    )

    add(
        context.getString(R.string.compose_ui),
        context.getString(R.string.compose_ui_url),
        context.getString(R.string.compose_ui_description),
    )

    add(
        context.getString(R.string.compose_animation),
        context.getString(R.string.compose_animation_url),
        context.getString(R.string.compose_animation_description),
    )

    add(
        context.getString(R.string.compose_material_3),
        context.getString(R.string.compose_material_3_url),
        context.getString(R.string.compose_material_3_description),
    )

    add(
        context.getString(R.string.compose_ui_tooling),
        context.getString(R.string.compose_ui_tooling_url),
        context.getString(R.string.compose_ui_tooling_description),
    )

    addArchLibraries(context)
    addBillingLibraries(context)
    addBootstrapLibraries(context)
    addUtilLibraries(context)
    addThemeLibraries(context)
  }

  private fun addThemeLibraries(context: Context) {
    if (addedTheme) {
      return
    }
    addedTheme = true

    add(
        context.getString(R.string.pydroid_theme),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_theme_description),
    )

    add(
        context.getString(R.string.compose_ui),
        context.getString(R.string.compose_ui_url),
        context.getString(R.string.compose_ui_description),
    )
    add(
        context.getString(R.string.compose_material_3),
        context.getString(R.string.compose_material_3_url),
        context.getString(R.string.compose_material_3_description),
    )

    addCoreLibraries(context)
  }

  private fun addArchLibraries(context: Context) {
    if (addedArch) {
      return
    }
    addedArch = true

    add(
        context.getString(R.string.pydroid_arch),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_arch_description),
    )

    add(
        context.getString(R.string.compose_runtime_saveable),
        context.getString(R.string.compose_runtime_saveable_url),
        context.getString(R.string.compose_runtime_saveable_description),
    )

    addBusLibraries(context)
    addUtilLibraries(context)
  }

  private fun addBusLibraries(context: Context) {
    if (addedBus) {
      return
    }
    addedBus = true

    add(
        context.getString(R.string.pydroid_bus),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_bus_description),
    )

    addCoreLibraries(context)
  }

  private fun addBillingLibraries(context: Context) {
    if (addedBilling) {
      return
    }
    addedBilling = true

    add(
        context.getString(R.string.pydroid_billing),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_billing_description),
    )

    add(
        context.getString(R.string.androidx_activity),
        context.getString(R.string.androidx_activity_url),
        context.getString(R.string.androidx_activity_description),
    )

    add(
        context.getString(R.string.google_play_in_app_billing_library),
        context.getString(R.string.google_play_url),
        context.getString(R.string.google_play_in_app_billing_library_description),
        license =
            OssLicenses.custom(
                license = context.getString(R.string.custom_google_license),
                location = context.getString(R.string.google_play_url),
            ),
    )

    add(
        context.getString(R.string.compose_runtime),
        context.getString(R.string.compose_runtime_url),
        context.getString(R.string.compose_runtime_description),
    )

    addBusLibraries(context)
    addUtilLibraries(context)
  }

  private fun addNotifyLibraries(context: Context) {
    if (addedNotify) {
      return
    }
    addedNotify = true

    add(
        context.getString(R.string.pydroid_notify),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_notify_description),
    )

    add(
        context.getString(R.string.androidx_core),
        context.getString(R.string.androidx_core_url),
        context.getString(R.string.androidx_core_description),
    )

    addCoreLibraries(context)
    addUtilLibraries(context)
  }

  private fun addAutopsyLibraries(context: Context) {
    if (addedAutopsy) {
      return
    }
    addedAutopsy = true

    add(
        context.getString(R.string.androidx_startup),
        context.getString(R.string.androidx_startup_url),
        context.getString(R.string.androidx_startup_description),
    )
    add(
        context.getString(R.string.pydroid_autopsy),
        context.getString(R.string.pydroid_url),
        context.getString(R.string.pydroid_autopsy_description),
    )
    add(
        context.getString(R.string.compose_ui),
        context.getString(R.string.compose_ui_url),
        context.getString(R.string.compose_ui_description),
    )
    add(
        context.getString(R.string.compose_material_3),
        context.getString(R.string.compose_material_3_url),
        context.getString(R.string.compose_material_3_description),
    )

    add(
        context.getString(R.string.compose_ui_tooling),
        context.getString(R.string.compose_ui_tooling_url),
        context.getString(R.string.compose_ui_tooling_description),
    )

    addCoreLibraries(context)
    addThemeLibraries(context)
  }

  /** Add a new library to the list of libraries used by the application */
  @JvmStatic
  @JvmOverloads
  public fun add(
      name: String,
      url: String,
      description: String,
      license: LibraryLicense = OssLicenses.APACHE2
  ) {
    val lib =
        OssLibrary(
            name = name,
            description = description,
            libraryUrl = url,
            licenseName = license.license,
            licenseUrl = license.location,
        )

    if (!libraries.contains { it.key == lib.key }) {
      libraries.add(lib)
    }
  }

  /** Get the list of libraries used in the application */
  @JvmStatic
  @CheckResult
  public fun libraries(context: Context): Set<OssLibrary> {
    // Since we are in the bootstrap module, this always happens
    addBootstrapLibraries(context)

    if (usingUtil) {
      addUtilLibraries(context)
    }

    if (usingArch) {
      addArchLibraries(context)
    }

    if (usingAutopsy) {
      addAutopsyLibraries(context)
    }

    if (usingBilling) {
      addBillingLibraries(context)
    }

    if (usingBus) {
      addBusLibraries(context)
    }

    if (usingNotify) {
      addNotifyLibraries(context)
    }

    if (usingTheme) {
      addThemeLibraries(context)
    }

    if (usingUi) {
      addUiLibraries(context)
    }

    return libraries
  }
}
