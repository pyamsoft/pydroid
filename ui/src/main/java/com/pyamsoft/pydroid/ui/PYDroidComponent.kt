/*
 * Copyright 2024 pyamsoft
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

package com.pyamsoft.pydroid.ui

import android.app.Application
import android.content.Context
import androidx.annotation.CheckResult
import coil.ImageLoader
import com.pyamsoft.pydroid.bootstrap.about.AboutModule
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogModule
import com.pyamsoft.pydroid.bootstrap.datapolicy.DataPolicyModule
import com.pyamsoft.pydroid.bootstrap.settings.SettingsModule
import com.pyamsoft.pydroid.bus.internal.DefaultEventBus
import com.pyamsoft.pydroid.core.Logger
import com.pyamsoft.pydroid.core.PYDroidLogger
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.core.createThreadEnforcer
import com.pyamsoft.pydroid.ui.debug.InAppDebugStatus
import com.pyamsoft.pydroid.ui.internal.about.AboutComponent
import com.pyamsoft.pydroid.ui.internal.app.AppComponent
import com.pyamsoft.pydroid.ui.internal.datapolicy.dialog.DataPolicyDialogComponent
import com.pyamsoft.pydroid.ui.internal.debug.DebugInteractorImpl
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLogLine
import com.pyamsoft.pydroid.ui.internal.debug.InAppDebugLoggerImpl
import com.pyamsoft.pydroid.ui.internal.haptics.AndroidViewHapticManager
import com.pyamsoft.pydroid.ui.internal.preference.PYDroidPreferencesImpl
import com.pyamsoft.pydroid.ui.internal.settings.reset.ResetComponent
import com.pyamsoft.pydroid.ui.internal.theme.ThemingImpl
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.util.isDebugMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal interface PYDroidComponent {

  @CheckResult fun plusApp(): AppComponent.Factory

  @CheckResult fun plusAbout(): AboutComponent.Factory

  @CheckResult fun plusDataPolicyDialog(): DataPolicyDialogComponent.Factory

  @CheckResult fun plusReset(): ResetComponent.Factory

  fun inject(logger: InAppDebugLoggerImpl)

  fun inject(hapticManager: AndroidViewHapticManager)

  interface Factory {

    @CheckResult fun create(params: Component.Parameters): Component
  }

  interface Component : PYDroidComponent {

    @CheckResult fun moduleProvider(): ModuleProvider

    data class Parameters
    internal constructor(
        override val privacyPolicyUrl: String,
        override val bugReportUrl: String,
        override val viewSourceUrl: String,
        override val termsConditionsUrl: String,
        override val version: Int,
        override val logger: PYDroidLogger?,
        internal val application: Application,
        internal val debug: PYDroid.DebugParameters,
    ) : PYDroid.BaseParameters
  }

  class ComponentImpl
  private constructor(
      params: Component.Parameters,
  ) : Component {

    private val enforcer = createThreadEnforcer(params.application.isDebugMode())

    private val context: Context = params.application

    private val imageLoader: ImageLoader by lazy { ImageLoader(params.application) }

    private val theming: Theming by lazy { ThemingImpl(preferences) }

    private val preferences by lazy {
      PYDroidPreferencesImpl(
          enforcer = enforcer,
          context = params.application,
          versionCode = params.version,
      )
    }

    private val inAppLogLines by lazy { MutableStateFlow(emptyList<InAppDebugLogLine>()) }

    private val debugInteractor by lazy {
      DebugInteractorImpl(
          enforcer = enforcer,
          context = params.application,
      )
    }

    private val aboutModule by lazy {
      AboutModule(
          AboutModule.Parameters(
              context = context,
          ))
    }

    private val dataPolicyModule by lazy {
      DataPolicyModule(
          DataPolicyModule.Parameters(
              context = context,
              preferences = preferences,
          ),
      )
    }

    private val changeLogModule by lazy {
      ChangeLogModule(
          ChangeLogModule.Parameters(
              context = context,
              preferences = preferences,
              isFakeChangeLogAvailable = params.debug.changeLogAvailable,
          ),
      )
    }

    private val appParams by lazy {
      AppComponent.Factory.Parameters(
          enforcer = enforcer,
          context = context,
          theming = theming,
          billingErrorBus = DefaultEventBus(),
          changeLogModule = changeLogModule,
          imageLoader = imageLoader,
          version = params.version,
          dataPolicyModule = dataPolicyModule,
          bugReportUrl = params.bugReportUrl,
          termsConditionsUrl = params.termsConditionsUrl,
          privacyPolicyUrl = params.privacyPolicyUrl,
          viewSourceUrl = params.viewSourceUrl,
          debug = params.debug,
          billingPreferences = preferences,
          debugPreferences = preferences,
          logLinesBus = inAppLogLines,
          debugInteractor = debugInteractor,
          hapticPreferences = preferences,
      )
    }

    private val aboutParams by lazy {
      AboutComponent.Factory.Parameters(
          module = aboutModule,
      )
    }

    private val dataPolicyParams by lazy {
      DataPolicyDialogComponent.Factory.Parameters(
          imageLoader = imageLoader,
          module = dataPolicyModule,
          privacyPolicyUrl = params.privacyPolicyUrl,
          termsConditionsUrl = params.termsConditionsUrl,
      )
    }

    private val resetParams by lazy {
      ResetComponent.Factory.Parameters(
          module =
              SettingsModule(
                  SettingsModule.Parameters(
                      context = context,
                  ),
              ),
      )
    }

    private val provider by lazy {
      object : ModuleProvider {

        private val modules by lazy {
          object : ModuleProvider.Modules {

            override fun imageLoader(): ImageLoader {
              return imageLoader
            }

            override fun theming(): Theming {
              return theming
            }

            override fun enforcer(): ThreadEnforcer {
              return enforcer
            }

            override fun inAppDebugStatus(): InAppDebugStatus {
              return preferences
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

    override fun plusApp(): AppComponent.Factory {
      return AppComponent.Impl.FactoryImpl(appParams)
    }

    override fun plusAbout(): AboutComponent.Factory {
      return AboutComponent.Impl.FactoryImpl(aboutParams)
    }

    override fun plusDataPolicyDialog(): DataPolicyDialogComponent.Factory {
      return DataPolicyDialogComponent.Impl.FactoryImpl(dataPolicyParams)
    }

    override fun plusReset(): ResetComponent.Factory {
      return ResetComponent.Impl.FactoryImpl(resetParams)
    }

    override fun moduleProvider(): ModuleProvider {
      return provider
    }

    override fun inject(logger: InAppDebugLoggerImpl) {
      logger.logLines = inAppLogLines
      logger.status = preferences
    }

    override fun inject(hapticManager: AndroidViewHapticManager) {
      hapticManager.preferences = preferences
    }

    class FactoryImpl internal constructor() : Factory {

      override fun create(params: Component.Parameters): Component {
        return ComponentImpl(params)
      }
    }
  }
}
