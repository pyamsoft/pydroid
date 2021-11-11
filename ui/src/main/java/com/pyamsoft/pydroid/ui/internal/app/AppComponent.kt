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
import androidx.activity.viewModels
import androidx.annotation.CheckResult
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import com.pyamsoft.pydroid.arch.createViewModelFactory
import com.pyamsoft.pydroid.billing.BillingModule
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyInteractor
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionModule
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.protection.Protection
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.internal.billing.BillingComponent
import com.pyamsoft.pydroid.ui.internal.billing.BillingDelegate
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogDelegate
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModel
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyDelegate
import com.pyamsoft.pydroid.ui.internal.datapolicy.DataPolicyViewModel
import com.pyamsoft.pydroid.ui.internal.protection.ProtectionDelegate
import com.pyamsoft.pydroid.ui.internal.rating.RatingDelegate
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckDelegate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeDialog
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeViewModel
import com.pyamsoft.pydroid.ui.theme.Theming

internal interface AppComponent {

  fun inject(activity: PYDroidActivity)

  @CheckResult fun plusBilling(): BillingComponent.DialogComponent.Factory

  fun inject(dialog: VersionUpgradeDialog)

  interface Factory {

    @CheckResult
    fun create(
        activity: PYDroidActivity,
        disableDataPolicy: Boolean,
    ): AppComponent

    data class Parameters
    internal constructor(
        internal val rootFactory: ViewModelProvider.Factory,
        internal val context: Context,
        internal val theming: Theming,
        internal val errorBus: EventBus<Throwable>,
        internal val changeLogInteractor: ChangeLogInteractor,
        internal val dataPolicyInteractor: DataPolicyInteractor,
        internal val composeTheme: ComposeThemeFactory,
        internal val imageLoader: ImageLoader,
        internal val isFake: Boolean,
        internal val protection: Protection,
        internal val version: Int,
        internal val isFakeUpgradeChecker: Boolean,
        internal val isFakeUpgradeAvailable: Boolean,
    )
  }

  class Impl
  private constructor(
      private val params: Factory.Parameters,
      private val pyDroidActivity: PYDroidActivity,
      private val disableDataPolicy: Boolean,
  ) : AppComponent {

    // Make this module each time since if it falls out of scope, the in-app billing system
    // will crash
    private val billingModule =
        BillingModule(
            BillingModule.Parameters(
                context = params.context.applicationContext,
                errorBus = params.errorBus,
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

    private val versionCheckFactory = createViewModelFactory {
      VersionCheckViewModel(versionModule.provideInteractor())
    }

    private val versionUpgradeFactory = createViewModelFactory {
      VersionUpgradeViewModel(versionModule.provideInteractor())
    }

    private val ratingFactory = createViewModelFactory {
      RatingViewModel(ratingModule.provideInteractor())
    }

    private val appInternalFactory = createViewModelFactory {
      AppInternalViewModel(
          disableDataPolicy = disableDataPolicy,
          dataPolicyInteractor = params.dataPolicyInteractor,
      )
    }

    override fun inject(activity: PYDroidActivity) {
      // Billing
      activity.billing = BillingDelegate(pyDroidActivity, billingModule.provideConnector())

      // Protection
      activity.protection = ProtectionDelegate(pyDroidActivity, params.protection)

      // Rating
      val ratingViewModel by activity.viewModels<RatingViewModel> { ratingFactory }
      activity.rating = RatingDelegate(pyDroidActivity, ratingViewModel)

      // Version Check
      val versionCheckViewModel by
          activity.viewModels<VersionCheckViewModel> { versionCheckFactory }
      activity.versionCheck = VersionCheckDelegate(pyDroidActivity, versionCheckViewModel)

      // Change Log
      val changeLogViewModel by activity.viewModels<ChangeLogViewModel> { params.rootFactory }
      activity.changeLog = ChangeLogDelegate(pyDroidActivity, changeLogViewModel)

      // Data Policy
      val dataPolicyViewModel by activity.viewModels<DataPolicyViewModel> { params.rootFactory }
      activity.dataPolicy = DataPolicyDelegate(pyDroidActivity, dataPolicyViewModel)

      // App Internal
      activity.factory = appInternalFactory
    }

    override fun plusBilling(): BillingComponent.DialogComponent.Factory {
      return BillingComponent.DialogComponent.Impl.FactoryImpl(
          billingModule,
          BillingComponent.Factory.Parameters(
              context = params.context,
              theming = params.theming,
              errorBus = params.errorBus,
              interactor = params.changeLogInteractor,
              composeTheme = params.composeTheme,
              imageLoader = params.imageLoader,
          ),
      )
    }

    override fun inject(dialog: VersionUpgradeDialog) {
      dialog.composeTheme = params.composeTheme
      dialog.factory = versionUpgradeFactory
    }

    class FactoryImpl internal constructor(private val params: Factory.Parameters) : Factory {

      override fun create(activity: PYDroidActivity, disableDataPolicy: Boolean): AppComponent {
        OssLibraries.usingUi = true
        return Impl(params, activity, disableDataPolicy)
      }
    }
  }
}
