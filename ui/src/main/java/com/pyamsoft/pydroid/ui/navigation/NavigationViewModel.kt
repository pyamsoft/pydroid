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

package com.pyamsoft.pydroid.ui.navigation

import android.content.ActivityNotFoundException
import com.pyamsoft.pydroid.arch.UiState
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.navigation.NavigationViewModel.NavigationState
import javax.inject.Inject

class NavigationViewModel @Inject internal constructor(
  private val schedulerProvider: SchedulerProvider,
  private val bus: EventBus<FailedNavigationEvent>
) : UiViewModel<NavigationState>(
    initialState = NavigationState(throwable = null)
) {

  override fun onBind() {
    bus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe { handleFailedNavigation(it.error) }
        .destroy()
  }

  private fun handleFailedNavigation(error: ActivityNotFoundException) {
    setUniqueState(error, old = { it.throwable }) { state, value -> state.copy(throwable = value) }
  }

  override fun onUnbind() {
  }

  fun failedNavigation(error: ActivityNotFoundException) {
    bus.publish(FailedNavigationEvent(error))
  }

  data class NavigationState(val throwable: ActivityNotFoundException?) : UiState

}
