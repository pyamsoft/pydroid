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
import android.support.annotation.RestrictTo.Scope.LIBRARY
import android.support.annotation.VisibleForTesting
import com.pyamsoft.pydroid.PYDroidModule
import timber.log.Timber

@RestrictTo(RestrictTo.Scope.LIBRARY) class PYDroid internal constructor(module: PYDroidModule) {
  private val component: PYDroidComponent = PYDroidComponentImpl.withModule(module)
  @get:RestrictTo(
      RestrictTo.Scope.LIBRARY) @get:CheckResult val isDebugMode: Boolean = module.isDebug

  init {

    UiLicenses.addLicenses()
    if (module.isDebug) {
      setStrictMode()
      Timber.plant(Timber.DebugTree())
    }
    Timber.i("Initialize PYDroid Injector singleton")
  }

  @RestrictTo(RestrictTo.Scope.LIBRARY) @CheckResult fun provideComponent(): PYDroidComponent {
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

  @RestrictTo(LIBRARY) companion object {

    @Volatile private var instance: PYDroid? = null

    @VisibleForTesting @JvmStatic @RestrictTo(LIBRARY) internal fun setTestInstance(
        instance: PYDroid) {
      synchronized(PYDroid::class.java) {
        this.instance = instance;
      }
    }

    @JvmStatic @RestrictTo(LIBRARY) fun get(): PYDroid {
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
    fun initialize(context: Context, debug: Boolean) {
      if (instance == null) {
        synchronized(PYDroid::class.java) {
          if (instance == null) {
            instance = PYDroid(PYDroidModule(context.applicationContext, debug))
          }
        }
      }

      if (instance == null) {
        throw RuntimeException("PYDroid initialization failed!")
      }
    }
  }
}
