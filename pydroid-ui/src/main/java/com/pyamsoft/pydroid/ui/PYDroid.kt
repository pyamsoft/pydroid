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

class PYDroid internal constructor(module: PYDroidModule) {

  @RestrictTo(
      RestrictTo.Scope.LIBRARY) private val component: PYDroidComponent = PYDroidComponentImpl.withModule(
      module)

  val isDebugMode: Boolean = module.isDebug
    @get:[RestrictTo(RestrictTo.Scope.LIBRARY) CheckResult] get

  init {
    if (module.isDebug) {
      setStrictMode()
      Timber.plant(Timber.DebugTree())
    }
    UiLicenses.addLicenses()
    Timber.i("Initialize PYDroid Injector singleton")
  }

  @RestrictTo(
      RestrictTo.Scope.LIBRARY) @CheckResult internal fun provideComponent(): PYDroidComponent {
    return component
  }

  /**
   * Sets strict mode flags when running in debug mode
   */
  private fun setStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDeath().permitDiskReads().permitDiskWrites().penaltyFlashScreen().build())
    StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
  }

  companion object {

    @Volatile private var instance: PYDroid? = null

    @CheckResult @JvmStatic internal fun get(): PYDroid {
      if (instance == null) {
        synchronized(PYDroid::class.java) {
          if (instance == null) {
            throw NullPointerException("PYDroid instance must be initialized first")
          }
        }
      }

      return instance!!
    }

    /**
     * Initialize the library
     */
    @JvmStatic fun initialize(context: Context, debug: Boolean) {
      if (instance == null) {
        synchronized(PYDroid::class.java) {
          if (instance == null) {
            instance = PYDroid(PYDroidModule(context.applicationContext, debug))
          }
        }
      }

      if (instance == null) {
        synchronized(PYDroid::class.java) {
          if (instance == null) {
            throw RuntimeException("PYDroid initialization failed!")
          }
        }
      }
    }
  }
}
