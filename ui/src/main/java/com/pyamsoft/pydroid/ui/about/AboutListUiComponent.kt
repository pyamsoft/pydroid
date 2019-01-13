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
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.ui.about.AboutStateEvents.LicensesLoaded
import com.pyamsoft.pydroid.ui.about.AboutStateEvents.LoadComplete
import com.pyamsoft.pydroid.ui.about.AboutStateEvents.LoadError
import com.pyamsoft.pydroid.ui.about.AboutStateEvents.Loading
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerStateEvents
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerStateEvents.Hide
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerStateEvents.Show
import io.reactivex.Observable

class AboutListUiComponent internal constructor(
  private val owner: LifecycleOwner,
  private val listView: AboutListView,
  private val controllerBus: Listener<AboutStateEvents>,
  private val uiBus: Listener<AboutViewEvents>,
  private val spinnerBus: Publisher<SpinnerStateEvents>,
  private val schedulerProvider: SchedulerProvider
) : UiComponent<AboutViewEvents> {

  override fun id(): Int {
    return View.NO_ID
  }

  override fun create(savedInstanceState: Bundle?) {
    listView.inflate(savedInstanceState)

    owner.runOnDestroy {
      listView.teardown()
    }

    listenForControllerEvents()
  }

  override fun saveState(outState: Bundle) {
    listView.saveState(outState)
  }

  private fun listenForControllerEvents() {
    controllerBus.listen()
        .subscribe {
          when (it) {
            is Loading -> {
              startSpinner()
              listView.hide()
            }
            is LicensesLoaded -> {
              stopSpinner()
              listView.loadLicenses(it.libraries)
            }
            is LoadError -> {
              stopSpinner()
              listView.showError(it.error)
            }
            is LoadComplete -> {
              stopSpinner()
              listView.show()
            }
          }
        }
        .destroy(owner)
  }

  private fun startSpinner() {
    spinnerBus.publish(Show)
  }

  private fun stopSpinner() {
    spinnerBus.publish(Hide)
  }

  override fun onUiEvent(): Observable<AboutViewEvents> {
    return uiBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
  }

}