/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
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
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.isDebugMode

@ConsistentCopyVisibility
internal data class PYDroidInitializer
internal constructor(
    internal val component: PYDroidComponent,
    internal val moduleProvider: ModuleProvider
) {

  companion object {

    @JvmStatic
    internal fun create(
        application: Application,
        params: PYDroid.Parameters,
    ): PYDroidInitializer {

      if (application.isDebugMode()) {
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
                      logger = params.logger,
                      debug =
                          PYDroid.DebugParameters(
                              upgradeAvailable = params.debug?.upgradeAvailable ?: false,
                              changeLogAvailable = params.debug?.changeLogAvailable ?: false,
                          ),
                  ),
              )

      Logger.d { "Initializing PYDroid" }

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
