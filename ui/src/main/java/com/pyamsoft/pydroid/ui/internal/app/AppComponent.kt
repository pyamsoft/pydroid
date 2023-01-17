/*
 * Copyright 2022 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.internal.app

import android.content.Context
import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import coil.ImageLoader
import com.pyamsoft.pydroid.billing.BillingModule
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyModule
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionModule
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.ui.PYDroid.DebugParameters
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.billing.BillingUpsell
import com.pyamsoft.pydroid.ui.changelog.ShowUpdateChangeLog
import com.pyamsoft.pydroid.ui.datapolicy.ShowDataPolicy
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
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityComponents
import com.pyamsoft.pydroid.ui.internal.rating.MutableRatingViewState
import com.pyamsoft.pydroid.ui.internal.rating.RatingDelegate
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModeler
import com.pyamsoft.pydroid.ui.internal.settings.MutableSettingsViewState
import com.pyamsoft.pydroid.ui.internal.settings.SettingsComponent
import com.pyamsoft.pydroid.ui.internal.version.MutableVersionCheckViewState
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckDelegate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.internal.version.upgrade.MutableVersionUpgradeViewState
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeComponent
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionUpdateProgress
import com.pyamsoft.pydroid.ui.version.VersionUpgradeAvailable
import com.pyamsoft.pydroid.util.doOnCreate

internal interface AppComponent {

  @CheckResult fun create(activity: FragmentActivity): PYDroidActivityComponents

  @CheckResult fun plusBillingDialog(): BillingDialogComponent.Factory

  @CheckResult fun plusBilling(): BillingComponent.Factory

  @CheckResult fun plusChangeLog(): ChangeLogComponent.Factory

  @CheckResult fun plusDataPolicy(): DataPolicyComponent.Factory

  @CheckResult fun plusChangeLogDialog(): ChangeLogDialogComponent.Factory

  @CheckResult fun plusVersionCheck(): VersionCheckComponent.Factory

  @CheckResult fun plusVersionUpgrade(): VersionUpgradeComponent.Factory

  @CheckResult fun plusSettings(): SettingsComponent.Factory

  interface Factory {

    @CheckResult fun create(options: PYDroidActivityOptions): AppComponent

    data class Parameters
    internal constructor(
        internal val billingPreferences: BillingPreferences,
        internal val context: Context,
        internal val theming: Theming,
        internal val bugReportUrl: String,
        internal val viewSourceUrl: String,
        internal val privacyPolicyUrl: String,
        internal val termsConditionsUrl: String,
        internal val composeTheme: ComposeThemeFactory,
        internal val billingErrorBus: EventBus<Throwable>,
        internal val imageLoader: ImageLoader,
        internal val version: Int,
        internal val changeLogModule: ChangeLogModule,
        internal val dataPolicyModule: DataPolicyModule,
        internal val debug: DebugParameters,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
      private val options: PYDroidActivityOptions,
  ) : AppComponent {

    // Create these here to share between the Settings and PYDroidActivity screens
    private val versionCheckState = MutableVersionCheckViewState()

    // Make this module each time since if it falls out of scope, the in-app billing system
    // will crash
    private val billingModule =
        BillingModule(
            BillingModule.Parameters(
                context = params.context.applicationContext,
                errorBus = params.billingErrorBus,
            ),
        )

    // Make this module each time since if it falls out of scope, the in-app rating system
    // will crash
    private val ratingModule =
        RatingModule(
            RatingModule.Parameters(
                context = params.context.applicationContext,
            ),
        )

    // Make this module each time since if it falls out of scope, the in-app update system
    // will crash
    private val versionModule =
        VersionModule(
            VersionModule.Parameters(
                context = params.context.applicationContext,
                version = params.version,
                isFakeUpgradeAvailable = params.debug.upgradeAvailable,
            ),
        )

    private val billingDialogParams =
        BillingDialogComponent.Factory.Parameters(
            billingModule = billingModule,
            changeLogModule = params.changeLogModule,
            composeTheme = params.composeTheme,
            imageLoader = params.imageLoader,
            state = MutableBillingDialogViewState(),
        )

    private val billingParams =
        BillingComponent.Factory.Parameters(
            preferences = params.billingPreferences,
            state = MutableBillingViewState(),
            isFakeBillingUpsell = params.debug.showBillingUpsell,
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
            composeTheme = params.composeTheme,
            theming = params.theming,
            versionCheckState = versionCheckState,
            state = MutableSettingsViewState(),
        )

    private val changeLogParams =
        ChangeLogComponent.Factory.Parameters(
            changeLogModule = params.changeLogModule,
            state = MutableChangeLogViewState(),
        )

    private val changeLogDialogParams =
        ChangeLogDialogComponent.Factory.Parameters(
            changeLogModule = params.changeLogModule,
            composeTheme = params.composeTheme,
            imageLoader = params.imageLoader,
            version = params.version,
            state = MutableChangeLogDialogViewState(),
        )

    private val versionUpgradeParams =
        VersionUpgradeComponent.Factory.Parameters(
            module = versionModule,
            composeTheme = params.composeTheme,
            state = MutableVersionUpgradeViewState(params.version),
        )

    private val versionCheckParams =
        VersionCheckComponent.Factory.Parameters(
            module = versionModule,
            composeTheme = params.composeTheme,
            state = versionCheckState,
        )

    private val dataPolicyParams =
        DataPolicyComponent.Factory.Parameters(
            state = MutableDataPolicyViewState(),
            module = params.dataPolicyModule,
        )

    private fun connectBilling(activity: FragmentActivity) {
      if (options.disableBilling) {
        Logger.w("Application has disabled the billing component")
      } else {
        Logger.d("Attempt Billing Connection")
        billingModule.provideConnector().bind(activity)
      }
    }

    override fun create(activity: FragmentActivity): PYDroidActivityComponents {
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

      if (params.debug.tryShowInAppRating) {
        activity.doOnCreate {
          Logger.d("Try to force-show an In-App Rating")
          rating.loadInAppRating()
        }
      }

      return PYDroidActivityComponents(
          rating = rating,
          versionCheck =
              VersionCheckDelegate(
                  activity,
                  viewModel =
                      VersionCheckViewModeler(
                          state = versionCheckState,
                          interactor = versionModule.provideInteractor(),
                          interactorCache = versionModule.provideInteractorCache(),
                      ),
                  disabled = options.disableVersionCheck,
              ),
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
          versionUpdateProgress =
              VersionUpdateProgress.create(
                  activity,
                  disabled = options.disableVersionCheck,
              ),
      )
    }

    override fun plusVersionUpgrade(): VersionUpgradeComponent.Factory {
      return VersionUpgradeComponent.Impl.FactoryImpl(versionUpgradeParams)
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
