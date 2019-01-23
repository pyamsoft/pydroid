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

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LicensesLoaded
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LoadComplete
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LoadError
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.Loading
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.FailedViewLicenseExternal
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.destroy
import io.reactivex.Observable
import timber.log.Timber

class AboutListUiComponent internal constructor(
  private val listView: AboutListView,
  private val controllerBus: Listener<AboutStateEvent>,
  private val viewLicenseBus: Listener<LicenseStateEvent>,
  private val uiBus: Listener<AboutViewEvent>,
  private val schedulerProvider: SchedulerProvider,
  owner: LifecycleOwner
) : UiComponent<AboutViewEvent>(owner) {

  override fun id(): Int {
    return listView.id()
  }

  override fun create(savedInstanceState: Bundle?) {
    listView.inflate(savedInstanceState)
    owner.runOnDestroy { listView.teardown() }

    listenForControllerEvents()
  }

  override fun saveState(outState: Bundle) {
    listView.saveState(outState)
  }

  private fun listenForControllerEvents() {
    controllerBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Loading -> listView.hide()
            is LicensesLoaded -> listView.loadLicenses(it.libraries)
            is LoadError -> listView.showError(it.error)
            is LoadComplete -> listView.show()
          }
        }
        .destroy(owner)

    viewLicenseBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is FailedViewLicenseExternal -> listView.showError(it.error)
            else -> Timber.d("Ignored event: $it")
          }
        }
        .destroy(owner)
  }

  override fun onUiEvent(): Observable<AboutViewEvent> {
    return uiBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
  }

}