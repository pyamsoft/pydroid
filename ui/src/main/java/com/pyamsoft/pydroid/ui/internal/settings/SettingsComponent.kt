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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.bootstrap.version.VersionModule
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.haptics.HapticPreferences
import com.pyamsoft.pydroid.ui.internal.billing.BillingPreferences
import com.pyamsoft.pydroid.ui.internal.billing.BillingViewModeler
import com.pyamsoft.pydroid.ui.internal.billing.MutableBillingViewState
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModeler
import com.pyamsoft.pydroid.ui.internal.changelog.MutableChangeLogViewState
import com.pyamsoft.pydroid.ui.internal.debug.DebugPreferences
import com.pyamsoft.pydroid.ui.internal.version.MutableVersionCheckViewState
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.theme.Theming

internal interface SettingsComponent {

  fun inject(injector: SettingsInjector)

  interface Factory {

    @CheckResult fun create(): SettingsComponent

    @ConsistentCopyVisibility
    data class Parameters
    internal constructor(
        internal val state: MutableSettingsViewState,
        internal val versionCheckState: MutableVersionCheckViewState,
        internal val bugReportUrl: String,
        internal val viewSourceUrl: String,
        internal val privacyPolicyUrl: String,
        internal val termsConditionsUrl: String,
        internal val theming: Theming,
        internal val versionModule: VersionModule,
        internal val changeLogModule: ChangeLogModule,
        internal val options: PYDroidActivityOptions,
        internal val billingPreferences: BillingPreferences,
        internal val hapticPreferences: HapticPreferences,
        internal val billingState: MutableBillingViewState,
        internal val changeLogState: MutableChangeLogViewState,
        internal val debugPreferences: DebugPreferences,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
  ) : SettingsComponent {

    override fun inject(injector: SettingsInjector) {
      injector.options = params.options
      injector.viewModel =
          SettingsViewModeler(
              state = params.state,
              bugReportUrl = params.bugReportUrl,
              privacyPolicyUrl = params.privacyPolicyUrl,
              termsConditionsUrl = params.termsConditionsUrl,
              viewSourceUrl = params.viewSourceUrl,
              theming = params.theming,
              changeLogInteractor = params.changeLogModule.provideInteractor(),
              debugPreferences = params.debugPreferences,
              hapticPreferences = params.hapticPreferences,
          )
      injector.versionViewModel =
          VersionCheckViewModeler(
              state = params.versionCheckState,
              interactor = params.versionModule.provideInteractor(),
          )
      injector.billingViewModel =
          BillingViewModeler(
              state = params.billingState,
              preferences = params.billingPreferences,
              isFakeUpsell = params.debugPreferences.listenShowBillingUpsell(),
          )
      injector.changeLogViewModel =
          ChangeLogViewModeler(
              state = params.changeLogState,
              interactor = params.changeLogModule.provideInteractor(),
          )
    }

    internal class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(): SettingsComponent {
        return Impl(params)
      }
    }
  }
}
