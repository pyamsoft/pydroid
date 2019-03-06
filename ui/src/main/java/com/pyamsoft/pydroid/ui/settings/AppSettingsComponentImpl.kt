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

import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenterImpl
import com.pyamsoft.pydroid.ui.rating.RatingPresenterImpl
import com.pyamsoft.pydroid.ui.rating.ShowRating
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckPresenterImpl
import com.pyamsoft.pydroid.ui.version.VersionCheckState

internal class AppSettingsComponentImpl internal constructor(
  private val ratingInteractor: RatingInteractor,
  private val versionCheckInteractor: VersionCheckInteractor,
  private val theming: Theming,
  private val versionCheckBus: EventBus<VersionCheckState>,
  private val ratingStateBus: EventBus<ShowRating>,
  private val schedulerProvider: SchedulerProvider,
  private val preferenceScreen: PreferenceScreen,
  private val applicationName: String,
  private val bugreportUrl: String,
  private val hideClearAll: Boolean,
  private val hideUpgradeInformation: Boolean,
  private val failedNavBus: EventBus<FailedNavigationEvent>
) : AppSettingsComponent {

  override fun inject(fragment: AppSettingsPreferenceFragment) {
    val presenter = AppSettingsPresenterImpl(theming)
    val settingsView = AppSettingsView(
        theming, applicationName, bugreportUrl, hideClearAll,
        hideUpgradeInformation, preferenceScreen, presenter
    )

    fragment.apply {
      this.failedNavPresenter = FailedNavigationPresenterImpl(schedulerProvider, failedNavBus)
      this.settingsView = settingsView
      this.settingsPresenter = presenter
      this.versionPresenter = VersionCheckPresenterImpl(
          versionCheckInteractor, schedulerProvider, versionCheckBus
      )
      this.ratingPresenter = RatingPresenterImpl(
          ratingInteractor, schedulerProvider, ratingStateBus
      )
    }
  }
}
