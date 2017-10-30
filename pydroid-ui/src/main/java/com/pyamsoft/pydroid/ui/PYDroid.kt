/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pydroid.ui

import android.content.Context
import android.os.StrictMode
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.PYDroidModule
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
  private var debugMode = false

  private fun setStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDeath().permitDiskReads()
            .permitDiskWrites().penaltyFlashScreen().build())
    StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
  }

  @CheckResult
  internal fun obtain(context: Context): PYDroidComponent {
    init(context, debugMode)
    return component!!
  }

  /**
   * Return the DEBUG state of the library
   */
  @CheckResult
  fun isDebugMode(): Boolean {
    return debugMode
  }

  /**
   * Initialize the library
   */
  @JvmOverloads
  fun init(context: Context, debug: Boolean,
      allowReInitialize: Boolean = false) {
    if (component == null || allowReInitialize) {
      initialize(context, debug)
    }
  }

  private fun initialize(context: Context, debug: Boolean) {
    debugMode = debug
    component = PYDroidComponentImpl(PYDroidModule(context.applicationContext, debug))
    if (debug) {
      Timber.plant(Timber.DebugTree())
      setStrictMode()
    }
    UiLicenses.addLicenses()
  }
}
