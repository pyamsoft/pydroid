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
import com.pyamsoft.pydroid.bootstrap.network.NetworkModule
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.AboutComponent
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponent
import com.pyamsoft.pydroid.ui.app.dialog.ThemeDialogComponent
import com.pyamsoft.pydroid.ui.arch.PYDroidViewModelFactory
import com.pyamsoft.pydroid.ui.otherapps.OtherAppsComponent
import com.pyamsoft.pydroid.ui.otherapps.listitem.OtherAppsItemComponent
import com.pyamsoft.pydroid.ui.preference.PYDroidPreferencesImpl
import com.pyamsoft.pydroid.ui.privacy.PrivacyComponent
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponent
import com.pyamsoft.pydroid.ui.settings.clear.SettingsClearConfigComponent
import com.pyamsoft.pydroid.ui.settings.clear.SettingsClearConfigModule
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionComponent

internal interface PYDroidComponent {

    @CheckResult
    fun plusPrivacy(): PrivacyComponent.Factory

    @CheckResult
    fun plusAbout(): AboutComponent.Factory

    @CheckResult
    fun plusAboutItem(): AboutItemComponent.Factory

    @CheckResult
    fun plusOtherApps(): OtherAppsComponent.Factory

    @CheckResult
    fun plusOtherAppsItem(): OtherAppsItemComponent.Factory

    @CheckResult
    fun plusClearConfirmDialog(): SettingsClearConfigComponent.Factory

    @CheckResult
    fun plusVersion(): VersionComponent.Factory

    @CheckResult
    fun plusSettings(): AppSettingsComponent.Factory

    @CheckResult
    fun plusThemeDialog(): ThemeDialogComponent.Factory

    interface Factory {

        @CheckResult
        fun create(params: Component.Parameters): Component
    }

    interface Component : PYDroidComponent {

        @CheckResult
        fun moduleProvider(): ModuleProvider

        data class Parameters internal constructor(
            internal val application: Application,
            internal val debug: Boolean,
            internal val name: CharSequence,
            internal val sourceUrl: String,
            internal val reportUrl: String,
            internal val privacyPolicyUrl: String,
            internal val termsConditionsUrl: String,
            internal val version: Int
        )
    }

    class ComponentImpl private constructor(params: Component.Parameters) : Component {

        private val context = params.application
        private val preferences = PYDroidPreferencesImpl(params.application)
        private val theming = Theming(preferences)
        private val packageName = params.application.packageName

        private val loaderModule = LoaderModule(
            LoaderModule.Parameters(
                context = context.applicationContext
            )
        )

        private val settingsClearConfigModule = SettingsClearConfigModule(
            SettingsClearConfigModule.Parameters(
                context = context.applicationContext
            )
        )

        private val aboutModule = AboutModule()

        private val ratingModule = RatingModule(
            RatingModule.Parameters(
                context = context.applicationContext,
                debug = params.debug,
                preferences = preferences
            )
        )

        private val networkModule = NetworkModule(
            NetworkModule.Parameters(
                debug = params.debug
            )
        )

        private val versionCheckModule = VersionCheckModule(
            VersionCheckModule.Parameters(
                context = context.applicationContext,
                debug = params.debug,
                version = params.version
            )
        )

        private val otherAppsModule = OtherAppsModule(
            OtherAppsModule.Parameters(
                packageName = packageName,
                serviceCreator = networkModule.provideServiceCreator()
            )
        )

        private val viewModelFactory =
            PYDroidViewModelFactory(
                PYDroidViewModelFactory.Parameters(
                    name = params.name,
                    version = params.version,
                    theming = theming,
                    debug = params.debug,
                    interactors = PYDroidViewModelFactory.Parameters.Interactors(
                        rating = ratingModule.provideInteractor(),
                        about = aboutModule.provideInteractor(),
                        version = versionCheckModule.provideInteractor(),
                        otherApps = otherAppsModule.provideInteractor(),
                        settingsClearConfig = settingsClearConfigModule.provideInteractor()
                    )
                )
            )

        private val appSettingsParams = AppSettingsComponent.Factory.Parameters(
            applicationName = params.name,
            bugReportUrl = params.reportUrl,
            viewSourceUrl = params.sourceUrl,
            privacyPolicyUrl = params.privacyPolicyUrl,
            termsConditionsUrl = params.termsConditionsUrl,
            factory = viewModelFactory
        )

        private val versionCheckParams = VersionComponent.Factory.Parameters(
            factory = viewModelFactory
        )

        private val privacyParams = PrivacyComponent.Factory.Parameters(
            factory = viewModelFactory
        )

        private val aboutParams = AboutComponent.Factory.Parameters(
            factory = viewModelFactory
        )

        private val otherAppsParams = OtherAppsComponent.Factory.Parameters(
            factory = viewModelFactory
        )

        private val themeDialogParams = ThemeDialogComponent.Factory.Parameters(
            debug = params.debug
        )

        private val settingsClearConfigParams = SettingsClearConfigComponent.Factory.Parameters(
            factory = viewModelFactory
        )

        private val provider = object : ModuleProvider {
            override fun theming(): Theming {
                return theming
            }

            override fun imageLoader(): ImageLoader {
                return loaderModule.provideLoader()
            }
        }

        override fun plusThemeDialog(): ThemeDialogComponent.Factory {
            return ThemeDialogComponent.Impl.FactoryImpl(themeDialogParams)
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
            return OtherAppsItemComponent.Impl.FactoryImpl(loaderModule.provideLoader())
        }

        override fun plusAboutItem(): AboutItemComponent.Factory {
            return AboutItemComponent.Impl.FactoryImpl()
        }

        override fun plusClearConfirmDialog(): SettingsClearConfigComponent.Factory {
            return SettingsClearConfigComponent.Impl.FactoryImpl(settingsClearConfigParams)
        }

        override fun plusVersion(): VersionComponent.Factory {
            return VersionComponent.Impl.FactoryImpl(versionCheckParams)
        }

        override fun plusSettings(): AppSettingsComponent.Factory {
            return AppSettingsComponent.Impl.FactoryImpl(appSettingsParams)
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
