/*
 * Copyright 2017 Peter Kenji Yamanaka
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

import android.content.Context
import android.os.StrictMode
import android.support.annotation.CheckResult
import android.support.annotation.RestrictTo
import com.pyamsoft.pydroid.PYDroidModule
import com.pyamsoft.pydroid.ui.about.UiLicenses
import timber.log.Timber

object PYDroid {

  @RestrictTo(RestrictTo.Scope.LIBRARY) private var component: PYDroidComponent? = null
  @RestrictTo(RestrictTo.Scope.LIBRARY) private var debugMode = false

  @RestrictTo(
      RestrictTo.Scope.LIBRARY) @JvmStatic private fun guaranteeNonNull(): PYDroidComponent {
    val obj = component
    if (obj == null) {
      throw IllegalStateException("Component must undergo initialize(Context, Boolean) before use")
    } else {
      return obj
    }
  }

  /**
   * Return the DEBUG state of the library
   */
  @JvmStatic @CheckResult fun isDebugMode(): Boolean {
    guaranteeNonNull()
    return debugMode
  }

  /**
   * Initialize the library
   */
  @JvmOverloads @JvmStatic fun initialize(context: Context, debug: Boolean,
      allowReInitialize: Boolean = false) {
    debugMode = debug
    if (component == null || allowReInitialize) {
      component = PYDroidComponentImpl(PYDroidModule(context.applicationContext, debug))
      if (debug) {
        Timber.plant(Timber.DebugTree())
        setStrictMode()
      }
      UiLicenses.addLicenses()
    }
  }

  private fun setStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDeath().permitDiskReads().permitDiskWrites().penaltyFlashScreen().build())
    StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
  }

  /**
   * For use internally in the library
   */
  @RestrictTo(RestrictTo.Scope.LIBRARY) @JvmStatic internal fun with(
      func: (PYDroidComponent) -> Unit) {
    func(guaranteeNonNull())
  }
}
