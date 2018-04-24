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
import android.content.Context
import android.os.StrictMode
import android.support.annotation.CheckResult
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.UiLicenses
import timber.log.Timber

/**
 * PYDroid library entry point
 */
class PYDroid private constructor(
  application: Application,
  debug: Boolean
) {

  private val component: PYDroidComponent = PYDroidComponentImpl(application, debug)

  init {
    if (debug) {
      Timber.plant(Timber.DebugTree())
      setStrictMode()
    }
    UiLicenses.addLicenses()
  }

  /**
   * Exposed so that outside applications can take advantage of the ImageLoader instance and cache
   */
  @CheckResult
  fun loaderModule(): LoaderModule {
    return component.loaderModule()
  }

  interface Instance {

    @CheckResult
    fun getPydroid(): PYDroid?

    fun setPydroid(instance: PYDroid)

  }

  companion object {

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
    internal fun obtain(context: Context): PYDroidComponent {
      val app = context.applicationContext
      if (app is PYDroid.Instance) {
        return checkNotNull(app.getPydroid()?.component) {
          "PYDroid not initialized. call PYDroid.init(Application, PYDroid.Instance, Boolean)"
        }
      } else {
        throw IllegalStateException("Application does not implement PYDroid.Instance interface")
      }
    }

    /**
     * Initialize the library
     *
     * You should carry the passed modules with you to any other component graphs or you will have "doubled" singletons
     */
    @JvmStatic
    fun init(
      application: Application,
      instance: Instance,
      debug: Boolean
    ) {
      if (instance.getPydroid() == null) {
        instance.setPydroid(PYDroid(application, debug))
      }
    }
  }
}
