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
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.BroadcastViewLicense
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.BroadcastVisitHomepage
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LicensesLoaded
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LoadComplete
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.LoadError
import com.pyamsoft.pydroid.ui.about.AboutStateEvent.Loading
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.Complete
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.FailedViewLicenseExternal
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.Loaded
import com.pyamsoft.pydroid.ui.about.dialog.LicenseStateEvent.PageError
import com.pyamsoft.pydroid.ui.arch.BaseUiComponent
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.destroy
import timber.log.Timber

class AboutListUiComponent internal constructor(
  private val controllerBus: Listener<AboutStateEvent>,
  private val viewLicenseBus: Listener<LicenseStateEvent>,
  private val schedulerProvider: SchedulerProvider,
  view: AboutListView,
  owner: LifecycleOwner
) : BaseUiComponent<EMPTY, AboutListView>(view, owner) {

  override fun onCreate(savedInstanceState: Bundle?) {
    controllerBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Loading -> view.hide()
            is LicensesLoaded -> view.loadLicenses(it.libraries)
            is LoadError -> view.showError(it.error)
            is LoadComplete -> view.show()
            is BroadcastVisitHomepage, is BroadcastViewLicense -> {
              Timber.d("Ignoring event: $it")
            }
          }
        }
        .destroy(owner)

    viewLicenseBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is FailedViewLicenseExternal -> view.showError(it.error)
            is LicenseStateEvent.Loading, is Loaded, is PageError, is Complete -> {
              Timber.d("Ignoring event: $it")
            }
          }
        }
        .destroy(owner)
  }

}
