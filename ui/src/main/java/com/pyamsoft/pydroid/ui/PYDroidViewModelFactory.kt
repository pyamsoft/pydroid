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

import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UiViewModelFactory
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.ui.about.AboutListViewModel
import com.pyamsoft.pydroid.ui.about.AboutToolbarViewModel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewModel
import com.pyamsoft.pydroid.ui.settings.AppSettingsViewModel
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckViewModel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeViewModel

internal class PYDroidViewModelFactory internal constructor(
  private val ratingInteractor: RatingInteractor,
  private val aboutInteractor: AboutInteractor,
  private val versionInteractor: VersionCheckInteractor,
  private val theming: Theming,
  private val scheduler: SchedulerProvider
) : UiViewModelFactory() {

  override fun <T : UiViewModel<*, *, *>> viewModel(modelClass: Class<T>): UiViewModel<*, *, *> {
    return when (modelClass) {
      AboutToolbarViewModel::class.java -> AboutToolbarViewModel()
      AboutListViewModel::class.java -> AboutListViewModel(aboutInteractor, scheduler)
      RatingDialogViewModel::class.java -> RatingDialogViewModel(ratingInteractor, scheduler)
      AppSettingsViewModel::class.java -> AppSettingsViewModel(theming)
      VersionCheckViewModel::class.java -> VersionCheckViewModel(versionInteractor, scheduler)
      VersionUpgradeViewModel::class.java -> VersionUpgradeViewModel()
      else -> fail()
    }
  }

}
