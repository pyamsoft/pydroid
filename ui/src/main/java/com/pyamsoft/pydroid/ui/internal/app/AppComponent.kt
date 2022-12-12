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
import com.pyamsoft.pydroid.ui.PYDroid.DebugParameters
import com.pyamsoft.pydroid.ui.app.PYDroidActivityOptions
import com.pyamsoft.pydroid.ui.changelog.ShowUpdateChangeLog
import com.pyamsoft.pydroid.ui.internal.billing.BillingComponent
import com.pyamsoft.pydroid.ui.internal.billing.BillingDelegate
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogComponent
import com.pyamsoft.pydroid.ui.internal.changelog.MutableChangeLogViewState
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialogComponent
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyDelegate
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyViewModeler
import com.pyamsoft.pydroid.ui.internal.datapolicy.MutableDataPolicyViewState
import com.pyamsoft.pydroid.ui.internal.pydroid.PYDroidActivityComponents
import com.pyamsoft.pydroid.ui.internal.rating.MutableRatingViewState
import com.pyamsoft.pydroid.ui.internal.rating.RatingDelegate
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModeler
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

internal interface AppComponent {

  @CheckResult fun create(activity: FragmentActivity): PYDroidActivityComponents

  @CheckResult fun plusBilling(): BillingComponent.Factory

  @CheckResult fun plusChangeLog(): ChangeLogComponent.Factory

  @CheckResult fun plusChangeLogDialog(): ChangeLogDialogComponent.Factory

  @CheckResult fun plusVersionCheck(): VersionCheckComponent.Factory

  @CheckResult fun plusVersionUpgrade(): VersionUpgradeComponent.Factory

  @CheckResult fun plusSettings(): SettingsComponent.Factory

  interface Factory {

    @CheckResult fun create(options: PYDroidActivityOptions): AppComponent

    data class Parameters
    internal constructor(
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
    private val ratingViewState = MutableRatingViewState()
    private val versionCheckState = MutableVersionCheckViewState()
    private val versionUpgradeState = MutableVersionUpgradeViewState(params.version)
    private val dataPolicyState = MutableDataPolicyViewState()
    private val changeLogState = MutableChangeLogViewState()

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

    private val billingParams =
        BillingComponent.Factory.Parameters(
            billingModule = billingModule,
            changeLogModule = params.changeLogModule,
            composeTheme = params.composeTheme,
            imageLoader = params.imageLoader,
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
            dataPolicyModule = params.dataPolicyModule,
            composeTheme = params.composeTheme,
            theming = params.theming,
            versionCheckState = versionCheckState,
            dataPolicyState = dataPolicyState,
            changeLogState = changeLogState,
        )

    private val changeLogParams =
        ChangeLogComponent.Factory.Parameters(
            changeLogModule = params.changeLogModule,
        )

    private val changeLogDialogParams =
        ChangeLogDialogComponent.Factory.Parameters(
            changeLogModule = params.changeLogModule,
            composeTheme = params.composeTheme,
            imageLoader = params.imageLoader,
            version = params.version,
        )

    override fun create(activity: FragmentActivity): PYDroidActivityComponents {
      return PYDroidActivityComponents(
          billing =
              BillingDelegate(
                  activity,
                  connector = billingModule.provideConnector(),
                  disabled = options.disableBilling,
              ),
          rating =
              RatingDelegate(
                  activity,
                  RatingViewModeler(
                      state = ratingViewState,
                      interactor = ratingModule.provideInteractor(),
                  ),
                  disabled = options.disableRating,
              ),
          versionCheck =
              VersionCheckDelegate(
                  activity,
                  VersionCheckViewModeler(
                      state = versionCheckState,
                      interactor = versionModule.provideInteractor(),
                      interactorCache = versionModule.provideInteractorCache(),
                  ),
                  disabled = options.disableVersionCheck,
              ),
          dataPolicy =
              DataPolicyDelegate(
                  activity,
                  DataPolicyViewModeler(
                      state = dataPolicyState,
                      interactor = params.dataPolicyModule.provideInteractor(),
                  ),
                  disabled = options.disableDataPolicy,
              ),
          showUpdateChangeLog =
              ShowUpdateChangeLog.create(
                  activity,
                  disabled = options.disableChangeLog,
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
      return VersionUpgradeComponent.Impl.FactoryImpl(
          VersionUpgradeComponent.Factory.Parameters(
              module = versionModule,
              composeTheme = params.composeTheme,
              state = versionUpgradeState,
          ),
      )
    }

    override fun plusBilling(): BillingComponent.Factory {
      return BillingComponent.Impl.FactoryImpl(billingParams)
    }

    override fun plusVersionCheck(): VersionCheckComponent.Factory {
      return VersionCheckComponent.Impl.FactoryImpl(
          VersionCheckComponent.Factory.Parameters(
              module = versionModule,
              composeTheme = params.composeTheme,
              state = versionCheckState,
          ),
      )
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
