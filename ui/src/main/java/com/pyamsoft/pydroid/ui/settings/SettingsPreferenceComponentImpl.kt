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

import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckWorker
import com.pyamsoft.pydroid.ui.version.VersionStateEvent

internal class SettingsPreferenceComponentImpl internal constructor(
  private val ratingModule: RatingModule,
  private val versionCheckModule: VersionCheckModule,
  private val theming: Theming,
  private val versionStateCheckBus: EventBus<VersionStateEvent>,
  private val schedulerProvider: SchedulerProvider,
  owner: LifecycleOwner,
  preferenceScreen: PreferenceScreen,
  applicationName: String,
  bugreportUrl: String,
  hideClearAll: Boolean,
  hideUpgradeInformation: Boolean
) : SettingsPreferenceComponent {

  private val settingsPreferenceView by lazy {
    SettingsPreferenceViewImpl(
        owner, preferenceScreen, theming,
        applicationName, bugreportUrl,
        hideClearAll, hideUpgradeInformation
    )
  }

  override fun inject(fragment: SettingsPreferenceFragment) {
    fragment.theming = theming
    fragment.ratingViewModel = ratingModule.getViewModel()
    fragment.settingsPreferenceView = settingsPreferenceView
    fragment.versionWorker = VersionCheckWorker(
        versionCheckModule.interactor, versionStateCheckBus, schedulerProvider
    )
  }
}
