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

package com.pyamsoft.pydroid.ui.version.upgrade

import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.arch.UiComponent
import io.reactivex.Observable

internal class VersionUpgradeControlsUiComponent internal constructor(
  private val schedulerProvider: SchedulerProvider,
  uiBus: Listener<VersionUpgradeViewEvent>,
  view: VersionUpgradeControlView,
  owner: LifecycleOwner
) : UiComponent<VersionUpgradeViewEvent, VersionUpgradeControlView>(view, uiBus, owner) {

  override fun onUiEvent(): Observable<VersionUpgradeViewEvent> {
    return super.onUiEvent()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
  }
}
