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

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyListener
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.Loading
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.UpdateComplete
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.UpdateError
import com.pyamsoft.pydroid.ui.version.VersionStateEvent.UpdateFound
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeStateEvent
import com.pyamsoft.pydroid.ui.version.upgrade.VersionUpgradeStateEvent.FailedMarketLink
import timber.log.Timber

internal class VersionUiComponent internal constructor(
  private val controllerBus: Listener<VersionStateEvent>,
  private val dialogControllerBus: Listener<VersionUpgradeStateEvent>,
  private val schedulerProvider: SchedulerProvider,
  view: VersionView,
  owner: LifecycleOwner
) : UiComponent<EMPTY, VersionView>(view, EmptyListener, owner) {

  override fun onCreate(savedInstanceState: Bundle?) {
    controllerBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Loading -> showUpdating(it.forced)
            is UpdateComplete -> view.dismissUpdating()
            is UpdateFound, is UpdateError -> Timber.d("Ignoring event: $it")
          }
        }
        .destroy(owner)

    dialogControllerBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is FailedMarketLink -> view.showError(it.error)
          }
        }
        .destroy(owner)
  }

  private fun showUpdating(forced: Boolean) {
    if (forced) {
      view.showUpdating()
    }
  }

}
