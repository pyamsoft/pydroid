/*
 * Copyright 2022 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import android.app.Activity
import android.app.Application
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import com.pyamsoft.pydroid.core.PYDroidLogger
import com.pyamsoft.pydroid.ui.app.ComposeThemeProvider
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.app.invoke
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidApplicationInstallTracker
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming

/**
 * A Compose theme provider which does nothing
 *
 * Can't use object literal or we lose @Composable context
 */
private object NoopThemeProvider : ComposeThemeProvider {

  @Composable
  override fun Render(
      activity: Activity,
      themeProvider: ThemeProvider,
      content: @Composable () -> Unit,
  ) {
    NoopTheme(activity, content)
  }
}

/** PYDroid library entry point */
public class PYDroid
internal constructor(
    private val instance: PYDroidInitializer,
) {

  /** Resolve and return exposed Modules for this PYDroid instance */
  @CheckResult
  public fun modules(): ModuleProvider.Modules {
    return moduleProvider().get()
  }

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

  /** Override Application.getSystemService() with this to get the PYDroid object graph */
  @CheckResult
  @Deprecated("Do not use, use Application.installPYDroid() instead")
  public fun getSystemService(name: String): Any? =
      when (name) {
        PYDroidComponent::class.java.name -> instance.component
        Theming::class.java.name -> modules().theming()
        else -> null
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

      /** Theme for Composables */
      internal val theme: ComposeThemeProvider = NoopThemeProvider,

      /** Debug options */
      internal val debug: DebugParameters? = null,
  ) : BaseParameters

  /** PYDroid debugging parameters */
  public data class DebugParameters(
      /** Is general debugging enabled (log messages, testing behaviors) */
      internal val enabled: Boolean,

      /** Is there an upgrade ready to install? */
      internal val upgradeAvailable: Boolean,

      /** Should the user be prompted to rate the application? */
      internal val ratingAvailable: Boolean,
  )

  /** Base parameters for PYDroid */
  internal interface BaseParameters {
    val viewSourceUrl: String
    val bugReportUrl: String
    val privacyPolicyUrl: String
    val termsConditionsUrl: String
    val version: Int
    val logger: PYDroidLogger?
  }

  /** Static methods */
  public companion object {

    /**
     * Initialize the library
     *
     * Track the Instance at the application level, such as:
     *
     * ```
     * Application.kt
     *
     * private var pydroid: PYDroid? = null
     *
     * override fun onCreate() {
     *   this.pydroid = PYDroid.init(
     *     this,
     *     PYDroid.Parameters(
     *       name = getString(R.string.app_name),
     *       bugReportUrl = getString(R.string.bug_report),
     *       version = BuildConfig.VERSION_CODE,
     *       debug = PYDroid.DebugParameters( ... ),
     *     ),
     *   )
     * }
     *
     * override fun getSystemService(name: String): Any? {
     *   return pydroid?.getSystemService(name) ?: super.getSystemService(name)
     * }
     * ```
     *
     * Generally speaking, you should treat a PYDroid instance as a Singleton. If you create more
     * than one instance and attempt to swap them out at runtime, the behavior of the library and
     * its dependent components is completely undefined.
     */
    @JvmStatic
    @CheckResult
    @Deprecated("Do not use, use Application.installPYDroid() instead")
    public fun init(
        application: Application,
        params: Parameters,
    ): PYDroid {
      return application.internalInstallPYDroid(params)
    }
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
  PYDroidApplicationInstallTracker.install(self, internals)
  return internals
}

/**
 * Install PYDroid into an Application
 *
 * Don't need @CheckResult just in case modules are not used
 */
public fun Application.installPYDroid(
    params: PYDroid.Parameters,
): ModuleProvider {
  return internalInstallPYDroid(params).moduleProvider()
}
