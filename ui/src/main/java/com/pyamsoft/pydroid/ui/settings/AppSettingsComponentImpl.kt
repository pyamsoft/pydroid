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

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.rating.RatingModule
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckModule
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.rating.RatingStateEvent
import com.pyamsoft.pydroid.ui.rating.RatingWorker
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.version.VersionCheckWorker
import com.pyamsoft.pydroid.ui.version.VersionStateEvent

internal class AppSettingsComponentImpl internal constructor(
  private val view: View,
  private val owner: LifecycleOwner,
  private val ratingModule: RatingModule,
  private val versionCheckModule: VersionCheckModule,
  private val theming: Theming,
  private val versionStateBus: EventBus<VersionStateEvent>,
  private val ratingStateBus: EventBus<RatingStateEvent>,
  private val settingsViewBus: EventBus<AppSettingsViewEvent>,
  private val settingsStateBus: EventBus<AppSettingsStateEvent>,
  private val schedulerProvider: SchedulerProvider,
  private val preferenceScreen: PreferenceScreen,
  private val applicationName: String,
  private val bugreportUrl: String,
  private val hideClearAll: Boolean,
  private val hideUpgradeInformation: Boolean
) : AppSettingsComponent {

  override fun inject(fragment: AppSettingsPreferenceFragment) {
    val settingsView = AppSettingsView(
        view, theming, applicationName, bugreportUrl, hideClearAll,
        hideUpgradeInformation, owner, preferenceScreen, settingsViewBus
    )
    fragment.theming = theming
    fragment.versionWorker = VersionCheckWorker(
        versionCheckModule.interactor, schedulerProvider, versionStateBus
    )
    fragment.ratingWorker = RatingWorker(ratingModule.interactor, schedulerProvider, ratingStateBus)
    fragment.settingsComponent = AppSettingsUiComponent(
        settingsStateBus, schedulerProvider, settingsView, owner
    )

    fragment.settingsWorker = AppSettingsWorker(settingsStateBus)
  }
}
