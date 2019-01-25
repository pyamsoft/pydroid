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

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.settings.AppSettingsStateEvent.FailedLink
import io.reactivex.Observable

internal class AppSettingsUiComponent internal constructor(
  private val schedulerProvider: SchedulerProvider,
  private val controllerBus: Listener<AppSettingsStateEvent>,
  view: AppSettingsView,
  uiBus: Listener<AppSettingsViewEvent>,
  owner: LifecycleOwner
) : UiComponent<AppSettingsViewEvent, AppSettingsView>(view, uiBus, owner) {

  override fun onCreate(savedInstanceState: Bundle?) {
    controllerBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is FailedLink -> view.showError(it.error)
          }
        }
        .destroy(owner)
  }

  override fun onUiEvent(): Observable<AppSettingsViewEvent> {
    return super.onUiEvent()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
  }

}
