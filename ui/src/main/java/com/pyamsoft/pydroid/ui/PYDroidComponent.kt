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
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.internal.about.AboutComponent
import com.pyamsoft.pydroid.ui.internal.about.listitem.AboutItemComponent
import com.pyamsoft.pydroid.ui.internal.arch.PYDroidViewModelFactory
import com.pyamsoft.pydroid.ui.internal.billing.BillingComponent
import com.pyamsoft.pydroid.ui.internal.billing.listitem.BillingItemComponent
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogComponent
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.ChangeLogDialogComponent
import com.pyamsoft.pydroid.ui.internal.changelog.dialog.listitem.ChangeLogDialogItemComponent
import com.pyamsoft.pydroid.ui.internal.dialog.ThemeDialogComponent
import com.pyamsoft.pydroid.ui.internal.otherapps.OtherAppsComponent
import com.pyamsoft.pydroid.ui.internal.otherapps.listitem.OtherAppsItemComponent
import com.pyamsoft.pydroid.ui.internal.preference.PYDroidPreferencesImpl
import com.pyamsoft.pydroid.ui.internal.privacy.PrivacyComponent
import com.pyamsoft.pydroid.ui.internal.rating.RatingComponent
import com.pyamsoft.pydroid.ui.internal.settings.AppSettingsComponent
import com.pyamsoft.pydroid.ui.internal.settings.clear.SettingsClearConfigComponent
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckComponent
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.theme.ThemingImpl
import kotlinx.coroutines.MainScope

internal interface PYDroidComponent {

  @CheckResult fun plusBilling(): BillingComponent.Factory

  @CheckResult fun plusBillingItem(): BillingItemComponent.Factory

  @CheckResult fun plusPrivacy(): PrivacyComponent.Factory

  @CheckResult fun plusAbout(): AboutComponent.Factory

  @CheckResult fun plusAboutItem(): AboutItemComponent.Factory

  @CheckResult fun plusOtherApps(): OtherAppsComponent.Factory

  @CheckResult fun plusOtherAppsItem(): OtherAppsItemComponent.Factory

  @CheckResult fun plusClearConfirm(): SettingsClearConfigComponent.Factory

  @CheckResult fun plusChangeLog(): ChangeLogComponent.Factory

  @CheckResult fun plusChangeLogDialog(): ChangeLogDialogComponent.Factory

  @CheckResult fun plusChangeLogDialogItem(): ChangeLogDialogItemComponent.Factory

  @CheckResult fun plusVersionCheck(): VersionCheckComponent.Factory

  @CheckResult fun plusSettings(): AppSettingsComponent.Factory

  @CheckResult fun plusThemeDialog(): ThemeDialogComponent.Factory

  @CheckResult fun plusRating(): RatingComponent.Factory

  interface Factory {

    @CheckResult fun create(params: Component.Parameters): Component
  }

  interface Component : PYDroidComponent {

    @CheckResult fun moduleProvider(): ModuleProvider

    data class Parameters
    internal constructor(
        internal val application: Application,
        internal val sourceUrl: String,
        internal val reportUrl: String,
        internal val privacyPolicyUrl: String,
        internal val termsConditionsUrl: String,
        internal val version: Int,
        internal val debug: DebugParameters,
    )

    data class DebugParameters(
        internal val enabled: Boolean,
        internal val upgradeAvailable: Boolean,
        internal val ratingAvailable: Boolean,
    )
  }

  class ComponentImpl private constructor(params: Component.Parameters) : Component {

    private val scope = MainScope()
    private val context = params.application

    private val preferences by lazy {
      PYDroidPreferencesImpl(params.application, params.version, params.debug.ratingAvailable)
    }

    private val theming: Theming by lazy { ThemingImpl(preferences) }

    private val viewModelFactory by lazy {
      PYDroidViewModelFactory(
          PYDroidViewModelFactory.Parameters(
              theming = theming,
              aboutInteractor = aboutModule.provideInteractor(),
              changeLogInteractor = changeLogModule.provideInteractor(),
              otherAppsInteractor = otherAppsModule.provideInteractor(),
              settingsInteractor = settingsModule.provideInteractor()))
    }

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
              serviceCreator = networkModule.provideServiceCreator()))
    }

    private val changeLogModule by lazy(LazyThreadSafetyMode.NONE) {
      ChangeLogModule(
          ChangeLogModule.Parameters(
              context = context.applicationContext, preferences = preferences))
    }

    private val privacyParams by lazy(LazyThreadSafetyMode.NONE) {
      PrivacyComponent.Factory.Parameters(factory = viewModelFactory)
    }

    private val appSettingsParams by lazy(LazyThreadSafetyMode.NONE) {
      AppSettingsComponent.Factory.Parameters(
          bugReportUrl = params.reportUrl,
          viewSourceUrl = params.sourceUrl,
          privacyPolicyUrl = params.privacyPolicyUrl,
          termsConditionsUrl = params.termsConditionsUrl,
          factory = viewModelFactory)
    }

    private val aboutParams by lazy(LazyThreadSafetyMode.NONE) {
      AboutComponent.Factory.Parameters(factory = viewModelFactory)
    }

    private val clearSettingsParams by lazy(LazyThreadSafetyMode.NONE) {
      SettingsClearConfigComponent.Factory.Parameters(factory = viewModelFactory)
    }

    private val otherAppsParams by lazy(LazyThreadSafetyMode.NONE) {
      OtherAppsComponent.Factory.Parameters(factory = viewModelFactory)
    }

    private val otherAppItemParams by lazy(LazyThreadSafetyMode.NONE) {
      OtherAppsItemComponent.Factory.Parameters(imageLoader = loaderModule.provideLoader())
    }

    private val changeLogParams by lazy(LazyThreadSafetyMode.NONE) {
      ChangeLogComponent.Factory.Parameters(factory = viewModelFactory)
    }

    private val ratingParams by lazy(LazyThreadSafetyMode.NONE) {
      RatingComponent.Factory.Parameters(
          context = context.applicationContext,
          isFake = params.debug.enabled,
          preferences = preferences)
    }

    private val versionParams by lazy(LazyThreadSafetyMode.NONE) {
      VersionCheckComponent.Factory.Parameters(
          context = context.applicationContext,
          version = params.version,
          isFakeUpgradeChecker = params.debug.enabled,
          isFakeUpgradeAvailable = params.debug.upgradeAvailable)
    }

    private val changeLogDialogParams by lazy(LazyThreadSafetyMode.NONE) {
      ChangeLogDialogComponent.Factory.Parameters(
          imageLoader = loaderModule.provideLoader(),
          interactor = changeLogModule.provideInteractor())
    }

    private val billingParams by lazy(LazyThreadSafetyMode.NONE) {
      BillingComponent.Factory.Parameters(
          context = context.applicationContext,
          theming = theming,
          errorBus = EventBus.create(emitOnlyWhenActive = false),
          imageLoader = loaderModule.provideLoader(),
          interactor = changeLogModule.provideInteractor())
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

    override fun plusThemeDialog(): ThemeDialogComponent.Factory {
      return ThemeDialogComponent.Impl.FactoryImpl()
    }

    override fun plusPrivacy(): PrivacyComponent.Factory {
      return PrivacyComponent.Impl.FactoryImpl(privacyParams)
    }

    override fun plusAbout(): AboutComponent.Factory {
      return AboutComponent.Impl.FactoryImpl(aboutParams)
    }

    override fun plusOtherApps(): OtherAppsComponent.Factory {
      return OtherAppsComponent.Impl.FactoryImpl(otherAppsParams)
    }

    override fun plusOtherAppsItem(): OtherAppsItemComponent.Factory {
      return OtherAppsItemComponent.Impl.FactoryImpl(otherAppItemParams)
    }

    override fun plusAboutItem(): AboutItemComponent.Factory {
      return AboutItemComponent.Impl.FactoryImpl()
    }

    override fun plusClearConfirm(): SettingsClearConfigComponent.Factory {
      return SettingsClearConfigComponent.Impl.FactoryImpl(clearSettingsParams)
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

    override fun plusBillingItem(): BillingItemComponent.Factory {
      return BillingItemComponent.Impl.FactoryImpl()
    }

    override fun plusSettings(): AppSettingsComponent.Factory {
      return AppSettingsComponent.Impl.FactoryImpl(appSettingsParams)
    }

    override fun plusChangeLog(): ChangeLogComponent.Factory {
      return ChangeLogComponent.Impl.FactoryImpl(changeLogParams)
    }

    override fun plusChangeLogDialog(): ChangeLogDialogComponent.Factory {
      return ChangeLogDialogComponent.Impl.FactoryImpl(changeLogDialogParams)
    }

    override fun plusChangeLogDialogItem(): ChangeLogDialogItemComponent.Factory {
      return ChangeLogDialogItemComponent.Impl.FactoryImpl()
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
