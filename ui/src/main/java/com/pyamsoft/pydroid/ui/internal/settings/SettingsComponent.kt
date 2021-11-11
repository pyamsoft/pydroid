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

package com.pyamsoft.pydroid.ui.internal.settings

import androidx.annotation.CheckResult
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import com.pyamsoft.pydroid.arch.createViewModelFactory
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsModule
import com.pyamsoft.pydroid.ui.app.ComposeThemeFactory
import com.pyamsoft.pydroid.ui.settings.SettingsFragment
import com.pyamsoft.pydroid.ui.theme.Theming

internal interface SettingsComponent {

  fun inject(fragment: SettingsFragment)

  interface Factory {

    @CheckResult fun create(): SettingsComponent

    data class Parameters
    internal constructor(
        internal val bugReportUrl: String,
        internal val viewSourceUrl: String,
        internal val privacyPolicyUrl: String,
        internal val termsConditionsUrl: String,
        internal val factory: ViewModelProvider.Factory,
        internal val composeTheme: ComposeThemeFactory,
        internal val theming: Theming,
        internal val otherAppsModule: OtherAppsModule,
        internal val imageLoader: ImageLoader,
    )
  }

  class Impl private constructor(private val params: Factory.Parameters) : SettingsComponent {

    private val factory = createViewModelFactory {
      SettingsViewModel(
          bugReportUrl = params.bugReportUrl,
          privacyPolicyUrl = params.privacyPolicyUrl,
          termsConditionsUrl = params.termsConditionsUrl,
          viewSourceUrl = params.viewSourceUrl,
          theming = params.theming,
          interactor = params.otherAppsModule.provideInteractor(),
      )
    }

    override fun inject(fragment: SettingsFragment) {
      fragment.changeLogFactory = params.factory
      fragment.ratingFactory = params.factory
      fragment.versionFactory = params.factory
      fragment.dataPolicyFactory = params.factory
      fragment.factory = factory

      fragment.composeTheme = params.composeTheme
      fragment.imageLoader = params.imageLoader
    }

    internal class FactoryImpl internal constructor(private val params: Factory.Parameters) :
        Factory {

      override fun create(): SettingsComponent {
        return Impl(params)
      }
    }
  }
}
