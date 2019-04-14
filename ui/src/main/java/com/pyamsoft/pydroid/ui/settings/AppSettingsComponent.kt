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

package com.pyamsoft.pydroid.ui.settings

import androidx.annotation.CheckResult
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.ui.settings.AppSettingsComponent.AppSettingsModule
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Named

@Subcomponent(modules = [AppSettingsModule::class])
internal interface AppSettingsComponent {

  fun inject(fragment: AppSettingsPreferenceFragment)

  @Subcomponent.Factory
  interface Factory {

    @CheckResult
    fun create(
      @BindsInstance preferenceScreen: PreferenceScreen,
      @BindsInstance @Named("hide_clear_all") hideClearAll: Boolean,
      @BindsInstance @Named("hide_upgrade_info") hideUpgradeInformation: Boolean
    ): AppSettingsComponent

  }

  @Module
  abstract class AppSettingsModule {

    @Binds
    @CheckResult
    internal abstract fun bindUiComponent(impl: AppSettingsUiComponentImpl): AppSettingsUiComponent

    @Binds
    @CheckResult
    internal abstract fun bindUiCallback(impl: AppSettingsHandler): AppSettingsView.Callback

  }
}


