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

package com.pyamsoft.pydroid.ui.internal.app

import android.content.Context
import androidx.annotation.CheckResult
import coil.ImageLoader
import com.pyamsoft.pydroid.billing.BillingModule
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyModule
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionModule
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.protection.Protection
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.internal.billing.BillingComponent
import com.pyamsoft.pydroid.ui.internal.billing.BillingDelegate
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogDelegate
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModeler
import com.pyamsoft.pydroid.ui.internal.changelog.MutableChangeLogViewState
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogComponent
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyDelegate
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyViewModeler
import com.pyamsoft.pydroid.ui.internal.datapolicy.MutableDataPolicyViewState
import com.pyamsoft.pydroid.ui.internal.protection.ProtectionDelegate
import com.pyamsoft.pydroid.ui.internal.rating.MutableRatingViewState
import com.pyamsoft.pydroid.ui.internal.rating.RatingDelegate
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModeler
import com.pyamsoft.pydroid.ui.internal.settings.SettingsComponent
import com.pyamsoft.pydroid.ui.internal.version.MutableVersionCheckViewState
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckDelegate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModeler
import com.pyamsoft.pydroid.ui.internal.version.upgrade.MutableVersionUpgradeViewState
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeViewModeler
import com.pyamsoft.pydroid.ui.theme.Theming

internal interface AppComponent {

  fun inject(activity: PYDroidActivity)

  fun inject(dialog: VersionUpgradeDialog)

  @CheckResult fun plusBilling(): BillingComponent.Factory

  @CheckResult fun plusChangeLogDialog(): ChangeLogComponent.Factory

  @CheckResult fun plusVersionCheck(): VersionCheckComponent.Factory

  @CheckResult fun plusSettings(): SettingsComponent.Factory

  interface Factory {

    @CheckResult
    fun create(
        activity: PYDroidActivity,
        disableDataPolicy: Boolean,
        disableChangeLog: Boolean,
    ): AppComponent

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
        internal val isFake: Boolean,
        internal val protection: Protection,
        internal val version: Int,
        internal val isFakeUpgradeChecker: Boolean,
        internal val isFakeUpgradeAvailable: Boolean,
        internal val changeLogModule: ChangeLogModule,
        internal val otherAppsModule: OtherAppsModule,
        internal val dataPolicyModule: DataPolicyModule,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
      private val pyDroidActivity: PYDroidActivity,
      private val disableDataPolicy: Boolean,
      private val disableChangeLog: Boolean,
  ) : AppComponent {

    // Create these here to share between the Settings and PYDroidActivity screens
    private val versionCheckState = MutableVersionCheckViewState()
    private val ratingState = MutableRatingViewState()
    private val changeLogState = MutableChangeLogViewState()
    private val dataPolicyState = MutableDataPolicyViewState()

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
                isFake = params.isFake,
            ),
        )

    // Make this module each time since if it falls out of scope, the in-app update system
    // will crash
    private val versionModule =
        VersionModule(
            VersionModule.Parameters(
                context = params.context.applicationContext,
                version = params.version,
                isFakeUpgradeChecker = params.isFakeUpgradeChecker,
                isFakeUpgradeAvailable = params.isFakeUpgradeAvailable,
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
            ratingModule = ratingModule,
            versionModule = versionModule,
            bugReportUrl = params.bugReportUrl,
            termsConditionsUrl = params.termsConditionsUrl,
            privacyPolicyUrl = params.privacyPolicyUrl,
            viewSourceUrl = params.viewSourceUrl,
            changeLogModule = params.changeLogModule,
            dataPolicyModule = params.dataPolicyModule,
            otherAppsModule = params.otherAppsModule,
            composeTheme = params.composeTheme,
            theming = params.theming,
            versionCheckState = versionCheckState,
            dataPolicyState = dataPolicyState,
            ratingState = ratingState,
            changeLogState = changeLogState,
        )

    private val changeLogParams =
        ChangeLogComponent.Factory.Parameters(
            ratingModule = ratingModule,
            changeLogModule = params.changeLogModule,
            composeTheme = params.composeTheme,
            imageLoader = params.imageLoader,
        )

    override fun inject(activity: PYDroidActivity) {
      // Billing
      activity.billing = BillingDelegate(pyDroidActivity, billingModule.provideConnector())

      // Protection
      activity.protection = ProtectionDelegate(pyDroidActivity, params.protection)

      // Rating
      activity.rating =
          RatingDelegate(
              pyDroidActivity,
              RatingViewModeler(
                  state = ratingState,
                  interactor = ratingModule.provideInteractor(),
              ),
          )

      // Version Check
      activity.versionCheck =
          VersionCheckDelegate(
              pyDroidActivity,
              VersionCheckViewModeler(
                  state = versionCheckState,
                  interactor = versionModule.provideInteractor(),
              ),
          )

      // Change Log
      activity.changeLog =
          ChangeLogDelegate(
              pyDroidActivity,
              ChangeLogViewModeler(
                  state = changeLogState,
                  interactor = params.changeLogModule.provideInteractor(),
              ),
          )

      // Data Policy
      activity.dataPolicy =
          DataPolicyDelegate(
              pyDroidActivity,
              DataPolicyViewModeler(
                  state = dataPolicyState,
                  interactor = params.dataPolicyModule.provideInteractor(),
              ),
          )

      // App Internal
      activity.presenter =
          AppInternalPresenter(
              disableChangeLog = disableChangeLog,
              disableDataPolicy = disableDataPolicy,
              dataPolicyInteractor = params.dataPolicyModule.provideInteractor(),
              changeLogInteractor = params.changeLogModule.provideInteractor(),
          )
    }

    override fun inject(dialog: VersionUpgradeDialog) {
      dialog.composeTheme = params.composeTheme
      dialog.viewModel =
          VersionUpgradeViewModeler(
              state = MutableVersionUpgradeViewState(),
              interactor = versionModule.provideInteractor(),
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
          ),
      )
    }

    override fun plusSettings(): SettingsComponent.Factory {
      return SettingsComponent.Impl.FactoryImpl(settingsParams)
    }

    override fun plusChangeLogDialog(): ChangeLogComponent.Factory {
      return ChangeLogComponent.Impl.FactoryImpl(changeLogParams)
    }

    class FactoryImpl internal constructor(private val params: Factory.Parameters) : Factory {

      override fun create(
          activity: PYDroidActivity,
          disableDataPolicy: Boolean,
          disableChangeLog: Boolean,
      ): AppComponent {
        OssLibraries.usingUi = true
        return Impl(
            params,
            activity,
            disableDataPolicy,
            disableChangeLog,
        )
      }
    }
  }
}
