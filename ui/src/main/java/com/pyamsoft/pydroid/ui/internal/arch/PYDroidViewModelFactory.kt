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

package com.pyamsoft.pydroid.ui.internal.arch

import com.pyamsoft.pydroid.arch.UiStateViewModel
import com.pyamsoft.pydroid.arch.UiViewModelFactory
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.changelog.ChangeLogInteractor
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.bootstrap.settings.SettingsInteractor
import com.pyamsoft.pydroid.ui.internal.about.AboutViewModel
import com.pyamsoft.pydroid.ui.internal.changelog.ChangeLogViewModel
import com.pyamsoft.pydroid.ui.internal.otherapps.OtherAppsViewModel
import com.pyamsoft.pydroid.ui.internal.privacy.PrivacyViewModel
import com.pyamsoft.pydroid.ui.internal.settings.AppSettingsViewModel
import com.pyamsoft.pydroid.ui.internal.settings.clear.SettingsClearConfigViewModel
import com.pyamsoft.pydroid.ui.theme.Theming
import kotlin.reflect.KClass

internal class PYDroidViewModelFactory internal constructor(
    private val params: Parameters
) : UiViewModelFactory() {

    override fun <T : UiStateViewModel<*>> viewModel(modelClass: KClass<T>): UiStateViewModel<*> {
        val interactors = params.interactors
        return when (modelClass) {
            AppSettingsViewModel::class -> AppSettingsViewModel(
                params.theming,
                interactors.otherApps,
            )
            PrivacyViewModel::class -> PrivacyViewModel()
            OtherAppsViewModel::class -> OtherAppsViewModel(interactors.otherApps)
            SettingsClearConfigViewModel::class -> SettingsClearConfigViewModel(interactors.settings)
            AboutViewModel::class -> AboutViewModel(interactors.about)
            ChangeLogViewModel::class -> ChangeLogViewModel(interactors.changeLog)
            else -> fail()
        }
    }

    internal data class Parameters internal constructor(
        internal val version: Int,
        internal val theming: Theming,
        internal val interactors: Interactors
    ) {
        internal data class Interactors internal constructor(
            internal val about: AboutInteractor,
            internal val otherApps: OtherAppsInteractor,
            internal val settings: SettingsInteractor,
            internal val changeLog: ChangeLogInteractor,
        )
    }
}
