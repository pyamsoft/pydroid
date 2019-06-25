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
import android.os.Looper
import android.os.StrictMode
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

internal class PYDroidInitializer internal constructor(
  application: Application,
  applicationName: String,
  bugReportUrl: String,
  currentVersion: Int,
  debug: Boolean
) {

  internal val component: PYDroidComponent
  internal val moduleProvider: ModuleProvider

  init {
    setupAsyncMainThreadScheduler()

    if (debug) {
      Timber.plant(Timber.DebugTree())
      setStrictMode()
    }

    val impl = PYDroidComponent.ComponentImpl.FactoryImpl()
        .create(
            application,
            debug,
            applicationName,
            bugReportUrl,
            currentVersion
        )
    component = impl
    moduleProvider = impl
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

  }
}
