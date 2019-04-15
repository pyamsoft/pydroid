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

import android.app.Application
import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.rating.RatingPreferences
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.bus.RxBus
import com.pyamsoft.pydroid.core.threads.Enforcer
import com.pyamsoft.pydroid.loader.LoaderModule
import com.pyamsoft.pydroid.ui.PYDroidComponent.PYDroidModule
import com.pyamsoft.pydroid.ui.about.AboutComponent
import com.pyamsoft.pydroid.ui.about.AboutHandler.AboutHandlerEvent
import com.pyamsoft.pydroid.ui.about.AboutToolbarHandler.ToolbarHandlerEvent
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemComponent
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemHandler.AboutItemHandlerEvent
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.rating.ShowRating
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogComponent
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogHandler.RatingEvent
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponent
import com.pyamsoft.pydroid.ui.settings.AppSettingsHandler.AppSettingsEvent
import com.pyamsoft.pydroid.ui.version.VersionCheckState
import com.pyamsoft.pydroid.ui.version.VersionComponent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeComponent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeHandler.VersionHandlerEvent
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
      AboutModule::class,
      VersionCheckModule::class,
      RatingModule::class,
      LoaderModule::class,
      PYDroidModule::class
    ]
)
internal interface PYDroidComponent : ModuleProvider {

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

  @Component.Factory
  interface Factory {

    @CheckResult
    fun create(
      @BindsInstance application: Application,
      @BindsInstance @Named("debug") debug: Boolean,
      @BindsInstance @Named("application_name") applicationName: String,
      @BindsInstance @Named("bug_report_url") bugReportUrl: String,
      @BindsInstance @Named("current_version") currentVersion: Int,
      @BindsInstance schedulerProvider: SchedulerProvider
    ): PYDroidComponent

  }

  @Module
  abstract class PYDroidModule {

    @Binds
    @CheckResult
    internal abstract fun bindContext(application: Application): Context

    @Binds
    @CheckResult
    internal abstract fun bindRatingPreferences(impl: PYDroidPreferencesImpl): RatingPreferences

    @Module
    companion object {

      @JvmStatic
      @CheckResult
      @Provides
      @Named("package_name")
      internal fun providePackageName(context: Context): String {
        return context.packageName
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideEnforcer(@Named("debug") debug: Boolean): Enforcer {
        return Enforcer(debug)
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideNavigationBus(): EventBus<FailedNavigationEvent> {
        return RxBus.create()
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideShowRatingBus(): EventBus<ShowRating> {
        return RxBus.create()
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideVersionCheckBus(): EventBus<VersionCheckState> {
        return RxBus.create()
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideVersionHandlerBus(): EventBus<VersionHandlerEvent> {
        return RxBus.create()
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideAboutHandlerBus(): EventBus<AboutHandlerEvent> {
        return RxBus.create()
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideAboutItemHandlerBus(): EventBus<AboutItemHandlerEvent> {
        return RxBus.create()
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideRatingHandlerBus(): EventBus<RatingEvent> {
        return RxBus.create()
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideToolbarHandlerBus(): EventBus<ToolbarHandlerEvent> {
        return RxBus.create()
      }

      @JvmStatic
      @CheckResult
      @Provides
      @Singleton
      internal fun provideAppSettingsHandlerBus(): EventBus<AppSettingsEvent> {
        return RxBus.create()
      }

    }
  }
}
