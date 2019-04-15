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

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.UiEventHandler
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeHandler.VersionHandlerEvent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeHandler.VersionHandlerEvent.Cancel
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeHandler.VersionHandlerEvent.Upgrade
import io.reactivex.disposables.Disposable

internal class VersionUpgradeHandler internal constructor(
  private val schedulerProvider: SchedulerProvider,
  bus: EventBus<VersionHandlerEvent>
) : UiEventHandler<VersionHandlerEvent, VersionUpgradeControlView.Callback>(bus),
    VersionUpgradeControlView.Callback {

  override fun onUpgradeClicked() {
    publish(Upgrade)
  }

  override fun onCancelClicked() {
    publish(Cancel)
  }

  @CheckResult
  override fun handle(delegate: VersionUpgradeControlView.Callback): Disposable {
    return listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Upgrade -> delegate.onUpgradeClicked()
            is Cancel -> delegate.onCancelClicked()
          }
        }
  }

  sealed class VersionHandlerEvent {
    object Upgrade : VersionHandlerEvent()
    object Cancel : VersionHandlerEvent()
  }

}
