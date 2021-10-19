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
import android.os.StrictMode
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.ui.internal.app.NoopTheme
import com.pyamsoft.pydroid.util.isDebugMode

internal data class PYDroidInitializer
internal constructor(
    internal val component: PYDroidComponent,
    internal val moduleProvider: ModuleProvider
) {

  companion object {

    @JvmStatic
    internal fun create(application: Application, params: PYDroid.Parameters): PYDroidInitializer {
      val isDebug = params.debug?.enabled ?: application.isDebugMode()

      if (isDebug) {
        setStrictMode()
      }

      val impl =
          PYDroidComponent.ComponentImpl.FactoryImpl()
              .create(
                  PYDroidComponent.Component.Parameters(
                      application = application,
                      viewSourceUrl = params.viewSourceUrl,
                      bugReportUrl = params.bugReportUrl,
                      privacyPolicyUrl = params.privacyPolicyUrl,
                      termsConditionsUrl = params.termsConditionsUrl,
                      version = params.version,
                      theme = params.theme ?: NoopTheme,
                      logger = params.logger,
                      debug =
                          PYDroidComponent.Component.DebugParameters(
                              enabled = isDebug,
                              upgradeAvailable = params.debug?.upgradeAvailable ?: false,
                          ),
                  ),
              )

      Logger.d("Initializing PYDroid")

      return PYDroidInitializer(impl, impl.moduleProvider())
    }

    @JvmStatic
    private fun setStrictMode() {
      StrictMode.setThreadPolicy(
          StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyFlashScreen().build())

      StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
    }
  }
}
