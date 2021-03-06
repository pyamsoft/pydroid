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

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckView
import com.pyamsoft.pydroid.ui.settings.AppSettingsPreferenceFragment

internal interface AppSettingsComponent {

  fun inject(fragment: AppSettingsPreferenceFragment)

  interface Factory {

    @CheckResult
    fun create(
        owner: LifecycleOwner,
        preferenceScreen: PreferenceScreen,
        hideClearAll: Boolean,
        hideUpgradeInformation: Boolean,
        parentProvider: () -> ViewGroup
    ): AppSettingsComponent

    data class Parameters
    internal constructor(
        internal val bugReportUrl: String,
        internal val viewSourceUrl: String,
        internal val privacyPolicyUrl: String,
        internal val termsConditionsUrl: String,
        internal val factory: ViewModelProvider.Factory
    )
  }

  class Impl
  private constructor(
      private val parentProvider: () -> ViewGroup,
      private val owner: LifecycleOwner,
      private val hideClearAll: Boolean,
      private val hideUpgradeInformation: Boolean,
      private val preferenceScreen: PreferenceScreen,
      private val params: Factory.Parameters
  ) : AppSettingsComponent {

    override fun inject(fragment: AppSettingsPreferenceFragment) {
      fragment.factory = params.factory

      val versionView = VersionCheckView(owner, parentProvider)
      val settingsView =
          AppSettingsView(
              params.bugReportUrl,
              params.viewSourceUrl,
              params.privacyPolicyUrl,
              params.termsConditionsUrl,
              hideClearAll,
              hideUpgradeInformation,
              preferenceScreen)

      fragment.versionCheckView = versionView
      fragment.settingsView = settingsView
    }

    internal class FactoryImpl internal constructor(private val params: Factory.Parameters) :
        Factory {

      override fun create(
          owner: LifecycleOwner,
          preferenceScreen: PreferenceScreen,
          hideClearAll: Boolean,
          hideUpgradeInformation: Boolean,
          parentProvider: () -> ViewGroup
      ): AppSettingsComponent {
        return Impl(
            parentProvider, owner, hideClearAll, hideUpgradeInformation, preferenceScreen, params)
      }
    }
  }
}
