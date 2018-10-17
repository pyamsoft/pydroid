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
import android.os.Looper
import android.os.StrictMode
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

/**
 * PYDroid library entry point
 */
class PYDroid private constructor(
  application: Application,
  debug: Boolean,
  schedulerProvider: SchedulerProvider = SchedulerProvider.Default
) {

  private val impl = PYDroidComponentImpl(application, debug, schedulerProvider)

  init {
    setupAsyncMainThreadScheduler()

    if (debug) {
      Timber.plant(Timber.DebugTree())
      setStrictMode()
    }
  }

  /**
   * Exposed so that outside applications can take advantage of the Module singletons
   *
   * This is not exposed outside of the object created in the PYDroid.Instance as it is
   * intended to only be used during the construction of the application level object graph.
   */
  @CheckResult
  fun modules(): ModuleProvider {
    return impl
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
              .penaltyFlashScreen()
              .build()
      )
      StrictMode.setVmPolicy(
          StrictMode.VmPolicy.Builder()
              .detectAll()
              .penaltyLog()
              .penaltyDeath()
              .build()
      )
    }

    // Async main thread scheduler
    // https://medium.com/@sweers/rxandroids-new-async-api-4ab5b3ad3e93
    @JvmStatic
    private fun setupAsyncMainThreadScheduler() {
      RxAndroidPlugins.setInitMainThreadSchedulerHandler {
        AndroidSchedulers.from(Looper.getMainLooper(), true)
      }

      val async = AndroidSchedulers.from(Looper.getMainLooper(), true)
      RxAndroidPlugins.setMainThreadSchedulerHandler { async }
    }

    /**
     * Access point for library component graph
     *
     * PYDroid internal
     */
    @JvmStatic
    @CheckResult
    internal fun obtain(context: Context): PYDroidComponent {
      val app = context.applicationContext
      if (app is PYDroid.Instance) {
        return checkNotNull(app.getPydroid()?.impl) {
          "PYDroid not initialized. call PYDroid.init(Application, PYDroid.Instance, Boolean)"
        }
      } else {
        throw IllegalStateException("Application does not implement PYDroid.Instance interface")
      }
    }

    /**
     * Initialize the library
     *
     * Track the Instance at the application level, such as:
     *
     * PYDroid.init(this, this, DEBUG)
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
