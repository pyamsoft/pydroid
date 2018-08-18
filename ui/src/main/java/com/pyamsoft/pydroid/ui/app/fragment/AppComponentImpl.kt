/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.app.fragment

import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.ui.UiModule

internal class AppComponentImpl internal constructor(
  private val uiModule: UiModule,
  private val versionCheckModule: VersionCheckModule,
  private val ratingModule: RatingModule,
  private val currentVersion: Int
) : AppComponent {

  override fun inject(fragment: SettingsPreferenceFragment) {
    fragment.versionViewModel = versionCheckModule.getViewModel(currentVersion)
    fragment.ratingViewModel = ratingModule.getViewModel(currentVersion)
    fragment.linker = uiModule.provideLinker()
    fragment.viewModel = SettingsPreferenceViewModel(uiModule.provideLinkerErrorBus())
  }
}
