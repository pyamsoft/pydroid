/*
 * Copyright 2021 Peter Kenji Yamanaka
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

package com.pyamsoft.pydroid.ui.arch

import androidx.lifecycle.ViewModel
import com.pyamsoft.pydroid.arch.ViewModelFactory
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.bootstrap.settings.SettingsInteractor
import com.pyamsoft.pydroid.ui.internal.about.AboutViewModel
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModel
import com.pyamsoft.pydroid.ui.internal.otherapps.OtherAppsViewModel
import com.pyamsoft.pydroid.ui.internal.settings.AppSettingsViewModel
import com.pyamsoft.pydroid.ui.internal.settings.clear.SettingsClearConfigViewModel
import com.pyamsoft.pydroid.ui.theme.Theming
import kotlin.reflect.KClass


internal class PYDroidViewModelFactory internal constructor(
    private val params: Parameters,
) : ViewModelFactory() {

    private val viewModelProviders = mapOf<KClass<out ViewModel>, () -> ViewModel>(
        AboutViewModel::class to { AboutViewModel(params.aboutInteractor) },
        ChangeLogViewModel::class to { ChangeLogViewModel(params.changeLogInteractor) },
        OtherAppsViewModel::class to { OtherAppsViewModel(params.otherAppsInteractor) },
        AppSettingsViewModel::class to {
            AppSettingsViewModel(params.theming, params.otherAppsInteractor)
        },
        SettingsClearConfigViewModel::class to {
            SettingsClearConfigViewModel(params.settingsInteractor)
        }
    )

    override fun <T : ViewModel> createViewModel(modelClass: Class<T>): ViewModel {
        return viewModelProviders[modelClass.kotlin]?.invoke() ?: fail(modelClass)
    }

    internal data class Parameters internal constructor(
        internal val theming: Theming,
        internal val aboutInteractor: AboutInteractor,
        internal val changeLogInteractor: ChangeLogInteractor,
        internal val otherAppsInteractor: OtherAppsInteractor,
        internal val settingsInteractor: SettingsInteractor
    )
}