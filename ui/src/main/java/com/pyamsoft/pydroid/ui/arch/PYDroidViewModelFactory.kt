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
 *
 */

package com.pyamsoft.pydroid.ui.arch

import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UiViewModelFactory
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.ui.about.AboutViewModel
import com.pyamsoft.pydroid.ui.otherapps.OtherAppsViewModel
import com.pyamsoft.pydroid.ui.privacy.PrivacyViewModel
import com.pyamsoft.pydroid.ui.rating.RatingViewModel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewModel
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewModel
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewModel
import kotlin.reflect.KClass

internal class PYDroidViewModelFactory internal constructor(
    private val params: Parameters
) : UiViewModelFactory() {

    override fun <T : UiViewModel<*, *, *>> viewModel(modelClass: KClass<T>): UiViewModel<*, *, *> {
        return when (modelClass) {
            RatingDialogViewModel::class -> RatingDialogViewModel(
                params.ratingInteractor,
                params.debug
            )
            VersionUpgradeViewModel::class -> VersionUpgradeViewModel(
                params.name,
                params.version,
                params.debug
            )
            AppSettingsViewModel::class -> AppSettingsViewModel(
                params.theming,
                params.otherAppsInteractor,
                params.debug
            )
            VersionCheckViewModel::class -> VersionCheckViewModel(
                params.versionInteractor,
                params.debug
            )
            PrivacyViewModel::class -> PrivacyViewModel(params.debug)
            OtherAppsViewModel::class -> OtherAppsViewModel(
                params.otherAppsInteractor,
                params.debug
            )
            AboutViewModel::class -> AboutViewModel(params.aboutInteractor, params.debug)
            RatingViewModel::class -> RatingViewModel(params.ratingInteractor, params.debug)
            else -> fail()
        }
    }

    internal data class Parameters internal constructor(
        internal val name: CharSequence,
        internal val version: Int,
        internal val ratingInteractor: RatingInteractor,
        internal val aboutInteractor: AboutInteractor,
        internal val versionInteractor: VersionCheckInteractor,
        internal val otherAppsInteractor: OtherAppsInteractor,
        internal val theming: Theming,
        internal val debug: Boolean
    )
}
