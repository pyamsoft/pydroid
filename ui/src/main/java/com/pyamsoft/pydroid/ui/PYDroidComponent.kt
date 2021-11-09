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
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.bootstrap.network.NetworkModule
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsModule
import com.pyamsoft.pydroid.bootstrap.settings.SettingsModule
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.PYDroidLogger
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.protection.Protection
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory
import com.pyamsoft.pydroid.ui.app.ComposeThemeProvider
import com.pyamsoft.pydroid.ui.internal.about.AboutComponent
import com.pyamsoft.pydroid.ui.internal.app.ProtectionComponent
import com.pyamsoft.pydroid.ui.internal.arch.PYDroidViewModelFactory
import com.pyamsoft.pydroid.ui.internal.billing.BillingComponent
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogComponent
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialogComponent
import com.pyamsoft.pydroid.ui.internal.otherapps.OtherAppsComponent
import com.pyamsoft.pydroid.ui.internal.preference.PYDroidPreferencesImpl
import com.pyamsoft.pydroid.ui.internal.rating.RatingComponent
import com.pyamsoft.pydroid.ui.internal.settings.AppSettingsComponent
import com.pyamsoft.pydroid.ui.internal.settings.SettingsComponent
import com.pyamsoft.pydroid.ui.internal.settings.reset.ResetComponent
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.ThemingImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal interface PYDroidComponent {

  @CheckResult fun plusBilling(): BillingComponent.Factory

  @CheckResult fun plusAbout(): AboutComponent.Factory

  @CheckResult fun plusOtherApps(): OtherAppsComponent.Factory

  @CheckResult fun plusReset(): ResetComponent.Factory

  @CheckResult fun plusChangeLog(): ChangeLogComponent.Factory

  @CheckResult fun plusChangeLogDialog(): ChangeLogDialogComponent.Factory

  @CheckResult fun plusVersionCheck(): VersionCheckComponent.Factory

  @CheckResult fun plusAppSettings(): AppSettingsComponent.Factory

  @CheckResult fun plusSettings(): SettingsComponent.Factory

  @CheckResult fun plusRating(): RatingComponent.Factory

  @CheckResult fun plusProtection(): ProtectionComponent.Factory

  interface Factory {

    @CheckResult fun create(params: Component.Parameters): Component
  }

  interface Component : PYDroidComponent {

    @CheckResult fun moduleProvider(): ModuleProvider

    data class Parameters
    internal constructor(
        override val googlePlayLicenseVerificationKey: String,
        override val imageLoader: () -> coil.ImageLoader,
        override val privacyPolicyUrl: String,
        override val bugReportUrl: String,
        override val viewSourceUrl: String,
        override val termsConditionsUrl: String,
        override val version: Int,
        override val logger: PYDroidLogger?,
        internal val application: Application,
        internal val debug: DebugParameters,
        internal val theme: ComposeThemeProvider,
    ) : PYDroid.BaseParameters

    data class DebugParameters(
        internal val enabled: Boolean,
        internal val upgradeAvailable: Boolean,
    )
  }

  class ComponentImpl private constructor(params: Component.Parameters) : Component {

    private val context = params.application

    private val theming: Theming by lazy(LazyThreadSafetyMode.NONE) { ThemingImpl(preferences) }

    private val protection by lazy(LazyThreadSafetyMode.NONE) {
      Protection.create(
          licenseKey = params.googlePlayLicenseVerificationKey,
      )
    }

    private val imageLoader: coil.ImageLoader by lazy(LazyThreadSafetyMode.NONE) {
      params.imageLoader()
    }

    private val preferences by lazy(LazyThreadSafetyMode.NONE) {
      PYDroidPreferencesImpl(params.application, params.version)
    }

    private val composeTheme by lazy(LazyThreadSafetyMode.NONE) {
      ComposeThemeFactory(theming = theming, themeProvider = params.theme)
    }

    private val viewModelFactory by lazy(LazyThreadSafetyMode.NONE) {
      PYDroidViewModelFactory(
          PYDroidViewModelFactory.Parameters(
              theming = theming,
              aboutInteractor = aboutModule.provideInteractor(),
              changeLogInteractor = changeLogModule.provideInteractor(),
              otherAppsInteractor = otherAppsModule.provideInteractor(),
              settingsInteractor = settingsModule.provideInteractor(),
          ))
    }

    @Deprecated("Use Coil-Compose in Jetpack Compose UI")
    private val loaderModule by lazy(LazyThreadSafetyMode.NONE) {
      LoaderModule(LoaderModule.Parameters(context = context.applicationContext))
    }

    private val settingsModule by lazy(LazyThreadSafetyMode.NONE) {
      SettingsModule(SettingsModule.Parameters(context = context.applicationContext))
    }

    private val aboutModule by lazy(LazyThreadSafetyMode.NONE) { AboutModule() }

    private val networkModule by lazy(LazyThreadSafetyMode.NONE) {
      NetworkModule(NetworkModule.Parameters(addLoggingInterceptor = params.debug.enabled))
    }

    private val otherAppsModule by lazy(LazyThreadSafetyMode.NONE) {
      OtherAppsModule(
          OtherAppsModule.Parameters(
              context = context.applicationContext,
              packageName = context.applicationContext.packageName,
              serviceCreator = networkModule.provideServiceCreator(),
          ))
    }

    private val changeLogModule by lazy(LazyThreadSafetyMode.NONE) {
      ChangeLogModule(
          ChangeLogModule.Parameters(
              context = context.applicationContext,
              preferences = preferences,
          ))
    }

    private val appSettingsParams by lazy(LazyThreadSafetyMode.NONE) {
      AppSettingsComponent.Factory.Parameters(
          bugReportUrl = params.bugReportUrl,
          viewSourceUrl = params.viewSourceUrl,
          privacyPolicyUrl = params.privacyPolicyUrl,
          termsConditionsUrl = params.termsConditionsUrl,
          factory = viewModelFactory,
      )
    }

    private val settingsParams by lazy(LazyThreadSafetyMode.NONE) {
      SettingsComponent.Factory.Parameters(
          bugReportUrl = params.bugReportUrl,
          viewSourceUrl = params.viewSourceUrl,
          privacyPolicyUrl = params.privacyPolicyUrl,
          termsConditionsUrl = params.termsConditionsUrl,
          factory = viewModelFactory,
          composeTheme = composeTheme,
          theming = theming,
          otherAppsModule = otherAppsModule,
          imageLoader = imageLoader,
      )
    }

    private val aboutParams by lazy(LazyThreadSafetyMode.NONE) {
      AboutComponent.Factory.Parameters(
          factory = viewModelFactory,
          composeTheme = composeTheme,
          imageLoader = imageLoader,
      )
    }

    private val clearSettingsParams by lazy(LazyThreadSafetyMode.NONE) {
      ResetComponent.Factory.Parameters(
          factory = viewModelFactory,
          composeTheme = composeTheme,
      )
    }

    private val otherAppsParams by lazy(LazyThreadSafetyMode.NONE) {
      OtherAppsComponent.Factory.Parameters(
          factory = viewModelFactory,
          composeTheme = composeTheme,
          imageLoader = imageLoader,
      )
    }

    private val changeLogParams by lazy(LazyThreadSafetyMode.NONE) {
      ChangeLogComponent.Factory.Parameters(factory = viewModelFactory)
    }

    private val ratingParams by lazy(LazyThreadSafetyMode.NONE) {
      RatingComponent.Factory.Parameters(
          context = context.applicationContext,
          isFake = params.debug.enabled,
      )
    }

    private val versionParams by lazy(LazyThreadSafetyMode.NONE) {
      VersionCheckComponent.Factory.Parameters(
          context = context.applicationContext,
          version = params.version,
          isFakeUpgradeChecker = params.debug.enabled,
          isFakeUpgradeAvailable = params.debug.upgradeAvailable,
          composeTheme = composeTheme,
      )
    }

    private val changeLogDialogParams by lazy(LazyThreadSafetyMode.NONE) {
      ChangeLogDialogComponent.Factory.Parameters(
          interactor = changeLogModule.provideInteractor(),
          composeTheme = composeTheme,
          imageLoader = imageLoader,
      )
    }

    private val protectionParams by lazy(LazyThreadSafetyMode.NONE) {
      ProtectionComponent.Factory.Parameters(
          protection = protection,
      )
    }

    private val billingParams by lazy(LazyThreadSafetyMode.NONE) {
      BillingComponent.Factory.Parameters(
          context = context.applicationContext,
          theming = theming,
          errorBus = EventBus.create(),
          interactor = changeLogModule.provideInteractor(),
          composeTheme = composeTheme,
          imageLoader = imageLoader,
      )
    }

    private val provider by lazy(LazyThreadSafetyMode.NONE) {
      object : ModuleProvider {

        private val modules by lazy(LazyThreadSafetyMode.NONE) {
          object : ModuleProvider.Modules {
            override fun theming(): Theming {
              return theming
            }

            override fun imageLoader(): ImageLoader {
              return loaderModule.provideLoader()
            }
          }
        }

        override fun get(): ModuleProvider.Modules {
          return modules
        }
      }
    }

    init {
      params.logger?.also { Logger.setLogger(it) }

      MainScope().launch(context = Dispatchers.Default) { theming.init() }
    }

    override fun plusAbout(): AboutComponent.Factory {
      return AboutComponent.Impl.FactoryImpl(aboutParams)
    }

    override fun plusOtherApps(): OtherAppsComponent.Factory {
      return OtherAppsComponent.Impl.FactoryImpl(otherAppsParams)
    }

    override fun plusReset(): ResetComponent.Factory {
      return ResetComponent.Impl.FactoryImpl(clearSettingsParams)
    }

    override fun plusVersionCheck(): VersionCheckComponent.Factory {
      return VersionCheckComponent.Impl.FactoryImpl(versionParams)
    }

    override fun plusRating(): RatingComponent.Factory {
      return RatingComponent.Impl.FactoryImpl(ratingParams)
    }

    override fun plusBilling(): BillingComponent.Factory {
      return BillingComponent.Impl.FactoryImpl(billingParams)
    }

    override fun plusAppSettings(): AppSettingsComponent.Factory {
      return AppSettingsComponent.Impl.FactoryImpl(appSettingsParams)
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

    override fun plusProtection(): ProtectionComponent.Factory {
      return ProtectionComponent.Impl.FactoryImpl(protectionParams)
    }

    override fun moduleProvider(): ModuleProvider {
      return provider
    }

    class FactoryImpl internal constructor() : Factory {

      override fun create(params: Component.Parameters): Component {
        return ComponentImpl(params)
      }
    }
  }
}
