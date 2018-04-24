/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui

import android.app.Application
import android.os.StrictMode
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.ui.about.UiLicenses
import timber.log.Timber

/**
 * PYDroid library entry point
 *
 * This actually does not inherit from the SimpleInjector interface because we want the
 * obtain method to stay internal
 */
object PYDroid {

  private var component: PYDroidComponent? = null

  @JvmStatic
  private fun setStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .penaltyDeath()
            .permitDiskReads()
            .permitDiskWrites()
            .penaltyFlashScreen()
            .build()
    )
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
    )
  }

  /**
   * Access point for library component graph
   */
  @JvmStatic
  @CheckResult
  internal fun obtain(): PYDroidComponent {
    val obj = component
    if (obj == null) {
      throw IllegalStateException(
          "PYDroid is not initialized. Please call PYDroid.init() before attempting to obtain."
      )
    } else {
      return obj
    }
  }

  /**
   * Create the library entry point
   */
  @JvmStatic
  private fun initialize(
    application: Application,
    debug: Boolean
  ) {
    component = PYDroidComponentImpl(application, debug)
    if (debug) {
      Timber.plant(Timber.DebugTree())
      setStrictMode()
    }
    UiLicenses.addLicenses()
  }

  /**
   * Initialize the library
   *
   * You should carry the passed modules with you to any other component graphs or you will have "doubled" singletons
   */
  @JvmStatic
  fun init(
    application: Application,
    debug: Boolean
  ) {
    if (component == null) {
      initialize(application, debug)
    }
  }
}
