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
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import com.pyamsoft.pydroid.ui.internal.about.AboutViewModel
import com.pyamsoft.pydroid.ui.internal.otherapps.OtherAppsViewModel
import com.pyamsoft.pydroid.ui.internal.privacy.PrivacyViewModel
import com.pyamsoft.pydroid.ui.internal.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.internal.settings.AppSettingsViewModel
import com.pyamsoft.pydroid.bootstrap.settings.SettingsClearConfigInteractor
import com.pyamsoft.pydroid.ui.internal.settings.clear.SettingsClearConfigViewModel
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.internal.version.upgrade.VersionUpgradeViewModel
import kotlin.reflect.KClass

internal class PYDroidViewModelFactory internal constructor(
    private val params: Parameters
) : UiViewModelFactory() {

    override fun <T : UiStateViewModel<*>> viewModel(modelClass: KClass<T>): UiStateViewModel<*> {
        val interactors = params.interactors
        return when (modelClass) {
            VersionUpgradeViewModel::class -> VersionUpgradeViewModel(interactors.version)
            AppSettingsViewModel::class -> AppSettingsViewModel(
                params.theming,
                interactors.otherApps,
            )
            VersionCheckViewModel::class -> VersionCheckViewModel(interactors.version)
            PrivacyViewModel::class -> PrivacyViewModel()
            OtherAppsViewModel::class -> OtherAppsViewModel(interactors.otherApps)
            SettingsClearConfigViewModel::class -> SettingsClearConfigViewModel(interactors.settingsClearConfig)
            AboutViewModel::class -> AboutViewModel(interactors.about)
            RatingViewModel::class -> RatingViewModel(interactors.rating)
            else -> fail()
        }
    }

    internal data class Parameters internal constructor(
        internal val name: CharSequence,
        internal val version: Int,
        internal val theming: Theming,
        internal val interactors: Interactors
    ) {
        internal data class Interactors internal constructor(
            internal val rating: RatingInteractor,
            internal val about: AboutInteractor,
            internal val version: VersionInteractor,
            internal val otherApps: OtherAppsInteractor,
            internal val settingsClearConfig: SettingsClearConfigInteractor
        )
    }
}
