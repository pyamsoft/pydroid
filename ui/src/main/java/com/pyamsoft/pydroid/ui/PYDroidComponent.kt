/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pydroid.ui

import android.app.Activity
import android.app.Application
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.about.AboutComponent
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponent
import com.pyamsoft.pydroid.ui.privacy.PrivacyComponent
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponent
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponent
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionComponent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponent

internal interface PYDroidComponent {

    @CheckResult
    fun plusPrivacy(): PrivacyComponent.Factory

    @CheckResult
    fun plusAbout(): AboutComponent.Factory

    @CheckResult
    fun plusAboutItem(): AboutItemComponent.Factory

    @CheckResult
    fun plusRatingDialog(): RatingDialogComponent.Factory

    @CheckResult
    fun plusVersion(): VersionComponent.Factory

    @CheckResult
    fun plusUpgrade(): VersionUpgradeComponent.Factory

    @CheckResult
    fun plusSettingsComponent(): AppSettingsComponent.Factory

    interface Factory {

        @CheckResult
        fun create(
            application: Application,
            debug: Boolean,
            applicationName: String,
            viewSourceUrl: String,
            bugReportUrl: String,
            privacyPolicyUrl: String,
            termsConditionsUrl: String,
            currentVersion: Int
        ): ComponentImpl
    }

    class ComponentImpl private constructor(
        application: Application,
        debug: Boolean,
        private val name: String,
        private val sourceUrl: String,
        private val reportUrl: String,
        private val privacyPolicyUrl: String,
        private val termsConditionsUrl: String,
        private val version: Int
    ) : PYDroidComponent, ModuleProvider {

        private val context = application
        private val enforcer = Enforcer(debug)
        private val preferences = PYDroidPreferencesImpl(context)
        private val theming = Theming(preferences)
        private val packageName = context.packageName

        private val aboutModule = AboutModule(enforcer)
        private val loaderModule = LoaderModule(context)
        private val ratingModule = RatingModule(version, enforcer, preferences)
        private val versionCheckModule =
            VersionCheckModule(debug, version, packageName, enforcer)

        @CheckResult
        private fun viewModelFactory(activity: Activity): PYDroidViewModelFactory {
            return PYDroidViewModelFactory(
                activity,
                ratingModule.provideInteractor(),
                aboutModule.provideInteractor(),
                versionCheckModule.provideInteractor(),
                theming
            )
        }

        override fun plusPrivacy(): PrivacyComponent.Factory {
            return PrivacyComponent.Impl.FactoryImpl { viewModelFactory(it) }
        }

        override fun plusAbout(): AboutComponent.Factory {
            return AboutComponent.Impl.FactoryImpl { viewModelFactory(it) }
        }

        override fun plusAboutItem(): AboutItemComponent.Factory {
            return AboutItemComponent.Impl.FactoryImpl()
        }

        override fun plusRatingDialog(): RatingDialogComponent.Factory {
            return RatingDialogComponent.Impl.FactoryImpl(loaderModule) { viewModelFactory(it) }
        }

        override fun plusVersion(): VersionComponent.Factory {
            return VersionComponent.Impl.FactoryImpl { viewModelFactory(it) }
        }

        override fun plusUpgrade(): VersionUpgradeComponent.Factory {
            return VersionUpgradeComponent.Impl.FactoryImpl(name, version) { viewModelFactory(it) }
        }

        override fun plusSettingsComponent(): AppSettingsComponent.Factory {
            return AppSettingsComponent.Impl.FactoryImpl(
                name, reportUrl, sourceUrl, privacyPolicyUrl, termsConditionsUrl
            ) { viewModelFactory(it) }
        }

        override fun enforcer(): Enforcer {
            return enforcer
        }

        override fun theming(): Theming {
            return theming
        }

        override fun imageLoader(): ImageLoader {
            return loaderModule.provideLoader()
        }

        class FactoryImpl internal constructor() : Factory {

            override fun create(
                application: Application,
                debug: Boolean,
                applicationName: String,
                viewSourceUrl: String,
                bugReportUrl: String,
                privacyPolicyUrl: String,
                termsConditionsUrl: String,
                currentVersion: Int
            ): ComponentImpl {
                return ComponentImpl(
                    application,
                    debug,
                    applicationName,
                    viewSourceUrl,
                    bugReportUrl,
                    privacyPolicyUrl,
                    termsConditionsUrl,
                    currentVersion
                )
            }
        }
    }
}
