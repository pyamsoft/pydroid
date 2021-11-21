/*
 * Copyright 2020 Peter Kenji Yamanaka
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

import android.app.Application
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.PYDroidLogger
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.app.ComposeThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming
import java.util.concurrent.atomic.AtomicReference

/** PYDroid library entry point */
public object PYDroid {

  private val instance = AtomicReference<PYDroidInitializer?>(null)

  /**
   * Access point for library versionComponent graph
   *
   * PYDroid internal
   */
  @JvmStatic
  @CheckResult
  private fun instance(): PYDroidInitializer {
    return instance.get().requireNotNull {
      "PYDroid not initialized, call PYDroid.init() in Application.onCreate()"
    }
  }

  /**
   * Initialize the library
   *
   * Track the Instance at the application level, such as:
   *
   * PYDroid.init(this, PYDroid.Parameters(
   * ```
   *    name = getString(R.string.app_name),
   *    bugReportUrl = getString(R.string.bug_report),
   *    version = BuildConfig.VERSION_CODE,
   *    debug = BuildConfig.DEBUG
   * ```
   * ))
   */
  @JvmStatic
  @CheckResult
  public fun init(application: Application, params: Parameters): ModuleProvider {
    if (instance.get() == null) {
      synchronized(this) {
        if (instance.get() == null) {
          val pydroid = PYDroidInitializer.create(application, params)
          if (instance.compareAndSet(null, pydroid)) {
            Logger.d("PYDroid is initialized.")
          }
        }
      }
    }

    return instance().moduleProvider
  }

  /** Override Application.getSystemService() with this to get the PYDroid object graph */
  @JvmStatic
  @CheckResult
  public fun getSystemService(name: String): Any? =
      when (name) {
        PYDroidComponent::class.java.name -> instance().component
        Theming::class.java.name -> instance().moduleProvider.get().theming()
        else -> null
      }

  /** PYDroid parameters */
  public data class Parameters
  @JvmOverloads
  public constructor(
      override val googlePlayLicenseVerificationKey: String,
      override val imageLoader: () -> coil.ImageLoader,
      override val viewSourceUrl: String,
      override val bugReportUrl: String,
      override val privacyPolicyUrl: String,
      override val termsConditionsUrl: String,
      override val version: Int,
      override val logger: PYDroidLogger? = null,
      internal val theme: ComposeThemeProvider? = null,
      internal val debug: DebugParameters? = null,
  ) : BaseParameters

  /** PYDroid debugging parameters */
  public data class DebugParameters(
      internal val enabled: Boolean,
      internal val upgradeAvailable: Boolean,
      internal val ratingAvailable: Boolean,
  )

  /** Base parameters for PYDroid */
  internal interface BaseParameters {
    val googlePlayLicenseVerificationKey: String
    val imageLoader: () -> coil.ImageLoader
    val viewSourceUrl: String
    val bugReportUrl: String
    val privacyPolicyUrl: String
    val termsConditionsUrl: String
    val version: Int
    val logger: PYDroidLogger?
  }
}
