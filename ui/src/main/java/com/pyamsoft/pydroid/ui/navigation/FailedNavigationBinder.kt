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
import com.pyamsoft.pydroid.arch.UiBinder
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus

class FailedNavigationBinder(
  private val schedulerProvider: SchedulerProvider,
  private val bus: EventBus<FailedNavigationEvent>
) : UiBinder<FailedNavigationBinder.Callback>() {

  override fun onBind() {
    bus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe { callback.handleFailedNavigation(it.error) }
        .destroy()
  }

  override fun onUnbind() {
  }

  fun failedNavigation(error: ActivityNotFoundException) {
    bus.publish(FailedNavigationEvent(error))
  }

  interface Callback : UiBinder.Callback {

    fun handleFailedNavigation(error: ActivityNotFoundException)

  }

}
