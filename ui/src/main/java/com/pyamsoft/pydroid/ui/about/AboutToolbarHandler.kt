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

package com.pyamsoft.pydroid.ui.about

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.UiEventHandler
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.about.AboutToolbarHandler.ToolbarHandlerEvent
import com.pyamsoft.pydroid.ui.about.AboutToolbarHandler.ToolbarHandlerEvent.Navigate
import io.reactivex.disposables.Disposable

internal class AboutToolbarHandler internal constructor(
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<ToolbarHandlerEvent>
) : UiEventHandler<ToolbarHandlerEvent, AboutToolbarView.Callback>(bus),
    AboutToolbarView.Callback {

  override fun onToolbarNavClicked() {
    publish(Navigate)
  }

  @CheckResult
  override fun handle(delegate: AboutToolbarView.Callback): Disposable {
    return listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.backgroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Navigate -> delegate.onToolbarNavClicked()
          }
        }
  }

  sealed class ToolbarHandlerEvent {
    object Navigate : ToolbarHandlerEvent()
  }

}
