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
import com.pyamsoft.pydroid.ui.internal.pydroid.ObjectGraph
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

  /**
   * Override Application.getSystemService() with this to get the PYDroid object graph
   *
   * See also [installPYDroid]
   */
  @CheckResult
  @Deprecated(
      """Do not use, use Application.installPYDroid() instead

PYDroid.init requires that an Application class call it at a very specific point, and override
the getSystemService() function in a very specific order which is prone to mistakes.

Users are instead encouraged to use the extension function Application.installPYDroid()
which will set up all of the expected bits of the old PYDroid.init but can be used anywhere,
including outside of an Application class.
""")
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
      /** Is there an upgrade ready to install? */
      internal val upgradeAvailable: Boolean = false,

      /** Should the user be shown the changelog? */
      internal val changeLogAvailable: Boolean = false,

      /** Should the user be shown a billing upsell ? */
      internal val showBillingUpsell: Boolean = false,

      /** Should we try to show the rating dialog? (Not always guaranteed) */
      internal val tryShowInAppRating: Boolean = false,
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
     *
     * See also [installPYDroid]
     */
    @JvmStatic
    @CheckResult
    @Deprecated(
        """Do not use, use Application.installPYDroid() instead

PYDroid.init requires that an Application class call it at a very specific point, and override
the getSystemService() function in a very specific order which is prone to mistakes.

Users are instead encouraged to use the extension function Application.installPYDroid()
which will set up all of the expected bits of the old PYDroid.init but can be used anywhere,
including outside of an Application class.
    """)
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
  ObjectGraph.ApplicationScope.install(self, internals)
  return internals
}

/**
 * Initialize the library
 *
 * Track the Instance at the application level, such as:
 *
 * ```
 * Application.kt
 *
 * override fun onCreate() {
 *   val optionalModuleProvider = installPYDroid(
 *     PYDroid.Parameters(
 *       name = getString(R.string.app_name),
 *       bugReportUrl = getString(R.string.bug_report),
 *       version = BuildConfig.VERSION_CODE,
 *       debug = PYDroid.DebugParameters( ... ),
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
 *       debug = PYDroid.DebugParameters( ... ),
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
