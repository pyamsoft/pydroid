/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui

import android.app.Application
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.ui.theme.Theming
import java.util.concurrent.atomic.AtomicReference

/**
 * PYDroid library entry point
 */
object PYDroid {

  private val DEFAULT_INIT_CALLBACK: (provider: ModuleProvider) -> Unit = {}

  private val instance = AtomicReference<PYDroidInitializer?>(null)

  /**
   * Access point for library versionComponent graph
   *
   * PYDroid internal
   */
  @JvmStatic
  @CheckResult
  private fun instance(): PYDroidInitializer {
    return requireNotNull(instance.get()) { "PYDroid not initialized, call PYDroid.init()" }
  }

  /**
   * Initialize the library
   *
   * Track the Instance at the application level, such as:
   *
   * PYDroid.init(
   *    this,
   *    this,
   *    getString(R.string.app_name),
   *    getString(R.string.bug_report),
   *    BuildConfig.VERSION_CODE,
   *    BuildConfig.DEBUG
   * )
   */
  @JvmStatic
  @JvmOverloads
  fun init(
    application: Application,
    applicationName: String,
    viewSourceUrl: String,
    bugReportUrl: String,
    currentVersion: Int,
    debug: Boolean,
    onInit: (provider: ModuleProvider) -> Unit = DEFAULT_INIT_CALLBACK
  ) {
    if (instance.get() == null) {
      val pydroid = PYDroidInitializer(
          application,
          applicationName,
          viewSourceUrl,
          bugReportUrl,
          currentVersion,
          debug
      )
      if (instance.compareAndSet(null, pydroid)) {
        onInit(pydroid.moduleProvider)
      }
    }
  }

  /**
   * Override Application.getSystemService() with this to get the PYDroid object graph
   */
  @JvmStatic
  @CheckResult
  fun getSystemService(name: String): Any? {
    return when (name) {
      Theming::class.java.name -> instance().moduleProvider.theming()
      PYDroidComponent::class.java.name -> instance().component
      Enforcer::class.java.name -> instance().moduleProvider.enforcer()
      else -> null
    }
  }
}
