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

package com.pyamsoft.pydroid.ui.internal.app

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.lifecycle.lifecycleScope
import coil3.ImageLoader
import com.pyamsoft.pydroid.billing.BillingModule
import com.pyamsoft.pydroid.billing.BillingPurchase
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyModule
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionModule
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.billing.BillingUpsell
import com.pyamsoft.pydroid.ui.changelog.ShowUpdateChangeLog
import com.pyamsoft.pydroid.ui.datapolicy.ShowDataPolicy
import com.pyamsoft.pydroid.ui.haptics.HapticPreferences
import com.pyamsoft.pydroid.ui.internal.billing.BillingComponent
import com.pyamsoft.pydroid.ui.internal.billing.BillingPreferences
import com.pyamsoft.pydroid.ui.internal.billing.MutableBillingViewState
import com.pyamsoft.pydroid.ui.internal.billing.dialog.BillingDialogComponent
import com.pyamsoft.pydroid.ui.internal.billing.dialog.MutableBillingDialogViewState
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogComponent
import com.pyamsoft.pydroid.ui.internal.changelog.MutableChangeLogViewState
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialogComponent
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.MutableChangeLogDialogViewState
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyComponent
import com.pyamsoft.pydroid.ui.internal.datapolicy.MutableDataPolicyViewState
import com.pyamsoft.pydroid.ui.internal.debug.DebugComponent
import com.pyamsoft.pydroid.ui.internal.debug.DebugInteractor
import com.pyamsoft.pydroid.ui.internal.debug.DebugPreferences
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine
import com.pyamsoft.pydroid.ui.internal.debug.MutableDebugViewState
import com.pyamsoft.pydroid.ui.internal.preference.MutablePreferenceViewState
import com.pyamsoft.pydroid.ui.internal.preference.PreferencesComponent
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityComponents
import com.pyamsoft.pydroid.ui.internal.rating.MutableRatingViewState
import com.pyamsoft.pydroid.ui.internal.rating.RatingDelegate
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModeler
import com.pyamsoft.pydroid.ui.internal.settings.MutableSettingsViewState
import com.pyamsoft.pydroid.ui.internal.settings.SettingsComponent
import com.pyamsoft.pydroid.ui.internal.version.MutableVersionCheckViewState
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionUpgradeAvailable
import com.pyamsoft.pydroid.util.Logger
import com.pyamsoft.pydroid.util.doOnCreate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal interface AppComponent {

  @CheckResult fun create(activity: ComponentActivity): PYDroidActivityComponents

  @CheckResult fun plusBillingDialog(): BillingDialogComponent.Factory

  @CheckResult fun plusBilling(): BillingComponent.Factory

  @CheckResult fun plusChangeLog(): ChangeLogComponent.Factory

  @CheckResult fun plusDataPolicy(): DataPolicyComponent.Factory

  @CheckResult fun plusChangeLogDialog(): ChangeLogDialogComponent.Factory

  @CheckResult fun plusVersionCheck(): VersionCheckComponent.Factory

  @CheckResult fun plusSettings(): SettingsComponent.Factory

  @CheckResult fun plusPreferences(): PreferencesComponent.Factory

  @CheckResult fun plusInAppDebug(): DebugComponent.Factory

  interface Factory {

    @CheckResult fun create(options: PYDroidActivityOptions): AppComponent

    @ConsistentCopyVisibility
    data class Parameters
    internal constructor(
        internal val hapticPreferences: HapticPreferences,
        internal val debugPreferences: DebugPreferences,
        internal val logLinesBus: StateFlow<List<InAppDebugLogLine>>,
        internal val debugInteractor: DebugInteractor,
        internal val billingPreferences: BillingPreferences,
        internal val context: Context,
        internal val theming: Theming,
        internal val bugReportUrl: String,
        internal val viewSourceUrl: String,
        internal val privacyPolicyUrl: String,
        internal val termsConditionsUrl: String,
        internal val billingErrorBus: EventBus<Throwable>,
        internal val billingPurchaseBus: EventBus<BillingPurchase>,
        internal val imageLoader: ImageLoader,
        internal val version: Int,
        internal val changeLogModule: ChangeLogModule,
        internal val dataPolicyModule: DataPolicyModule,
        internal val enforcer: ThreadEnforcer,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
      private val options: PYDroidActivityOptions,
  ) : AppComponent {

    // Create these here to share between the Settings and PYDroidActivity screens
    private val versionCheckState = MutableVersionCheckViewState()
    private val billingState = MutableBillingViewState()
    private val changeLogState = MutableChangeLogViewState()

    // Make this module each time since if it falls out of scope, the in-app billing system
    // will crash
    private val billingModule =
        BillingModule(
            BillingModule.Parameters(
                context = params.context.applicationContext,
                enforcer = params.enforcer,
                errorBus = params.billingErrorBus,
                purchaseBus = params.billingPurchaseBus,
            ),
        )

    // Make this module each time since if it falls out of scope, the in-app rating system
    // will crash
    private val ratingModule =
        RatingModule(
            RatingModule.Parameters(
                enforcer = params.enforcer,
                context = params.context.applicationContext,
            ),
        )

    // Make this module each time since if it falls out of scope, the in-app update system
    // will crash
    private val versionModule =
        VersionModule(
            VersionModule.Parameters(
                enforcer = params.enforcer,
                context = params.context.applicationContext,
                version = params.version,
                fakeUpgradeRequest = params.debugPreferences.listenUpgradeScenarioAvailable(),
            ),
        )

    private val billingDialogParams =
        BillingDialogComponent.Factory.Parameters(
            billingModule = billingModule,
            changeLogModule = params.changeLogModule,
            imageLoader = params.imageLoader,
            state = MutableBillingDialogViewState(),
        )

    private val billingParams =
        BillingComponent.Factory.Parameters(
            preferences = params.billingPreferences,
            state = billingState,
            isFakeBillingUpsell = params.debugPreferences.listenShowBillingUpsell(),
        )

    private val settingsParams =
        SettingsComponent.Factory.Parameters(
            options = options,
            versionModule = versionModule,
            bugReportUrl = params.bugReportUrl,
            termsConditionsUrl = params.termsConditionsUrl,
            privacyPolicyUrl = params.privacyPolicyUrl,
            viewSourceUrl = params.viewSourceUrl,
            changeLogModule = params.changeLogModule,
            theming = params.theming,
            versionCheckState = versionCheckState,
            state = MutableSettingsViewState(),
            billingPreferences = params.billingPreferences,
            billingState = billingState,
            changeLogState = changeLogState,
            debugPreferences = params.debugPreferences,
            hapticPreferences = params.hapticPreferences,
        )

    private val changeLogParams =
        ChangeLogComponent.Factory.Parameters(
            changeLogModule = params.changeLogModule,
            state = changeLogState,
        )

    private val changeLogDialogParams =
        ChangeLogDialogComponent.Factory.Parameters(
            changeLogModule = params.changeLogModule,
            imageLoader = params.imageLoader,
            version = params.version,
            state = MutableChangeLogDialogViewState(),
        )

    private val versionCheckParams =
        VersionCheckComponent.Factory.Parameters(
            module = versionModule,
            state = versionCheckState,
        )

    private val dataPolicyParams =
        DataPolicyComponent.Factory.Parameters(
            state = MutableDataPolicyViewState(),
            module = params.dataPolicyModule,
        )

    private val preferenceParams =
        PreferencesComponent.Factory.Parameters(
            state = MutablePreferenceViewState(),
        )

    private val inAppDebugParams =
        DebugComponent.Factory.Parameters(
            state =
                MutableDebugViewState(
                    logLinesBus = params.logLinesBus,
                ),
            preferences = params.debugPreferences,
            interactor = params.debugInteractor,
        )

    private fun connectBilling(activity: ComponentActivity) {
      if (options.disableBilling) {
        Logger.w { "Application has disabled the billing component" }
      } else {
        Logger.d { "Attempt Billing Connection" }
        billingModule.provideConnector().bind(activity)
      }
    }

    override fun create(activity: ComponentActivity): PYDroidActivityComponents {
      // Create rating here since we may use it to try force show an in-app rating
      val rating =
          RatingDelegate(
              activity,
              viewModel =
                  RatingViewModeler(
                      state = MutableRatingViewState(),
                      interactor = ratingModule.provideInteractor(),
                  ),
              disabled = options.disableRating,
          )

      // Connect the In-App Billing
      activity.doOnCreate { connectBilling(activity) }

      // Fake force-showing in-app rating
      activity.doOnCreate {
        params.debugPreferences.listenTryShowRatingUpsell().also { f ->
          activity.lifecycleScope.launch(context = Dispatchers.Main) {
            f.collect { show ->
              if (show) {
                Logger.d { "Try to force-show an In-App Rating" }
                rating.loadInAppRating()
              }
            }
          }
        }
      }

      return PYDroidActivityComponents(
          rating = rating,
          dataPolicy =
              ShowDataPolicy(
                  activity,
                  disabled = options.disableDataPolicy,
              ),
          showUpdateChangeLog =
              ShowUpdateChangeLog.create(
                  activity,
                  disabled = options.disableChangeLog,
              ),
          billingUpsell =
              BillingUpsell.create(
                  activity,
                  disabled = options.disableBilling,
              ),
          versionUpgrader =
              VersionUpgradeAvailable.create(
                  activity,
                  disabled = options.disableVersionCheck,
              ),
      )
    }

    override fun plusBillingDialog(): BillingDialogComponent.Factory {
      return BillingDialogComponent.Impl.FactoryImpl(billingDialogParams)
    }

    override fun plusBilling(): BillingComponent.Factory {
      return BillingComponent.Impl.FactoryImpl(billingParams)
    }

    override fun plusDataPolicy(): DataPolicyComponent.Factory {
      return DataPolicyComponent.Impl.FactoryImpl(dataPolicyParams)
    }

    override fun plusVersionCheck(): VersionCheckComponent.Factory {
      return VersionCheckComponent.Impl.FactoryImpl(versionCheckParams)
    }

    override fun plusSettings(): SettingsComponent.Factory {
      return SettingsComponent.Impl.FactoryImpl(settingsParams)
    }

    override fun plusChangeLog(): ChangeLogComponent.Factory {
      return ChangeLogComponent.Impl.FactoryImpl(changeLogParams)
    }

    override fun plusChangeLogDialog(): ChangeLogDialogComponent.Factory {
      return ChangeLogDialogComponent.Impl.FactoryImpl(changeLogDialogParams)
    }

    override fun plusPreferences(): PreferencesComponent.Factory {
      return PreferencesComponent.Impl.FactoryImpl(preferenceParams)
    }

    override fun plusInAppDebug(): DebugComponent.Factory {
      return DebugComponent.Impl.FactoryImpl(inAppDebugParams)
    }

    class FactoryImpl
    internal constructor(
        private val params: Factory.Parameters,
    ) : Factory {

      override fun create(
          options: PYDroidActivityOptions,
      ): AppComponent {
        OssLibraries.usingUi = true
        return Impl(params, options)
      }
    }
  }
}
