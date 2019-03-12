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

package com.pyamsoft.pydroid.ui.version

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenterImpl

internal class VersionComponentImpl internal constructor(
  private val owner: LifecycleOwner,
  private val view: View,
  private val bus: EventBus<VersionCheckState>,
  private val interactor: VersionCheckInteractor,
  private val schedulerProvider: SchedulerProvider,
  private val failedNavBus: EventBus<FailedNavigationEvent>
) : VersionComponent {

  override fun inject(activity: VersionCheckActivity) {
    val presenter = VersionCheckPresenterImpl(interactor, schedulerProvider, bus)
    val view = VersionView(view, owner)
    val failed = FailedNavigationPresenterImpl(schedulerProvider, failedNavBus)

    activity.apply {
      this.versionComponent = VersionCheckUiComponentImpl(failed, presenter, view)
    }
  }

}
