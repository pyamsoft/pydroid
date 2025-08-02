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

package com.pyamsoft.pydroid.ui

import android.app.Application
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
import com.pyamsoft.pydroid.util.PYDroidLogger

/** PYDroid library entry point */
public class PYDroid
internal constructor(
    private val instance: PYDroidInitializer,
) {

  /** Return module provider */
  @CheckResult
  internal fun moduleProvider(): ModuleProvider {
    return instance.moduleProvider
  }

  /** Return injector component */
  @CheckResult
  internal fun injector(): PYDroidComponent {
    return instance.component
  }

  /** Resolve and return exposed Modules for this PYDroid instance */
  @CheckResult
  public fun modules(): ModuleProvider.Modules {
    return moduleProvider().get()
  }

  /** PYDroid parameters */
  public data class Parameters
  @JvmOverloads
  public constructor(
      /** URL to view application source code */
      override val viewSourceUrl: String,

      /** URL to submit application bug reports */
      override val bugReportUrl: String,

      /** URL for privacy policy */
      override val privacyPolicyUrl: String,

      /** URL for TOS */
      override val termsConditionsUrl: String,

      /** Application version code */
      override val version: Int,

      /** Logger implementation */
      override val logger: PYDroidLogger? = null,
  ) : InternalParameters

  /** Parameters for PYDroid */
  internal interface InternalParameters {
    val viewSourceUrl: String
    val bugReportUrl: String
    val privacyPolicyUrl: String
    val termsConditionsUrl: String
    val version: Int
    val logger: PYDroidLogger?
  }
}

@CheckResult
private fun createPYDroidApplication(
    application: Application,
    params: PYDroid.Parameters,
): PYDroid {
  val instance = PYDroidInitializer.create(application, params)
  return PYDroid(instance)
}

/** We can remove this method once PYDroid.init() is gone */
@CheckResult
private fun Application.internalInstallPYDroid(
    params: PYDroid.Parameters,
): PYDroid {
  val self = this
  val internals = createPYDroidApplication(self, params)
  ObjectGraph.ApplicationScope.install(self, internals)
  return internals
}

/**
 * Initialize the library
 *
 * Track the Instance at the application level, such as:
 * ```
 * Application.kt
 *
 * override fun onCreate() {
 *   val optionalModuleProvider = installPYDroid(
 *     PYDroid.Parameters(
 *       name = getString(R.string.app_name),
 *       bugReportUrl = getString(R.string.bug_report),
 *       version = BuildConfig.VERSION_CODE,
 *     ),
 *   )
 * }
 * ```
 *
 * OR
 *
 * ```
 * Activity.kt
 *
 * override fun onCreate(savedInstanceState: Bundle?) {
 *   val optionalModuleProvider = application.installPYDroid(
 *     PYDroid.Parameters(
 *       name = getString(R.string.app_name),
 *       bugReportUrl = getString(R.string.bug_report),
 *       version = BuildConfig.VERSION_CODE,
 *     ),
 *   )
 * }
 *
 * ```
 *
 * Generally speaking, you should only call installPYDroid once per application lifecycle.
 *
 * Returns a delegate that can optionally be saved or used in the to resolve PYDroid initialized
 * singletons like Activity Theming or the internal Coil ImageLoader
 */
public fun Application.installPYDroid(
    params: PYDroid.Parameters,
): ModuleProvider {
  return internalInstallPYDroid(params).moduleProvider()
}
