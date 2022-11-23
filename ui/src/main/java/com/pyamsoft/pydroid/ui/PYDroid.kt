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
import coil.ImageLoader
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.PYDroidLogger
import com.pyamsoft.pydroid.ui.app.ComposeThemeProvider
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.ui.internal.app.invoke
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
private constructor(
    private val instance: PYDroidInitializer,
) {

  /** Override Application.getSystemService() with this to get the PYDroid object graph */
  @CheckResult
  public fun getSystemService(name: String): Any? =
      when (name) {
        PYDroidComponent::class.java.name -> instance.component
        Theming::class.java.name -> instance.moduleProvider.get().theming()
        else -> null
      }

  /** PYDroid parameters */
  public data class Parameters
  @JvmOverloads
  public constructor(
      /** The Coil image loader instance */
      override val lazyImageLoader: Lazy<ImageLoader>,

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
    val lazyImageLoader: Lazy<ImageLoader>
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
    public fun init(application: Application, params: Parameters): PYDroid {
      val instance = PYDroidInitializer.create(application, params)
      Logger.d("Initialize new PYDroid instance: $instance")
      return PYDroid(instance)
    }
  }
}
