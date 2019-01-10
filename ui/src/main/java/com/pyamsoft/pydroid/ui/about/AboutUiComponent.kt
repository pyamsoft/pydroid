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
import com.pyamsoft.pydroid.ui.about.AboutStateEvents.LicensesLoaded
import com.pyamsoft.pydroid.ui.about.AboutStateEvents.LoadComplete
import com.pyamsoft.pydroid.ui.about.AboutStateEvents.LoadError
import com.pyamsoft.pydroid.ui.about.AboutStateEvents.Loading
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.widget.RefreshLatch
import io.reactivex.Observable

class AboutUiComponent internal constructor(
  private val owner: LifecycleOwner,
  private val listView: AboutListView,
  private val loadingView: AboutLoadingView,
  private val controllerBus: Listener<AboutStateEvents>,
  private val uiBus: Listener<AboutViewEvents>,
  private val schedulerProvider: SchedulerProvider
) : UiComponent<AboutViewEvents> {

  private lateinit var refreshLatch: RefreshLatch

  override fun create(savedInstanceState: Bundle?) {
    listView.inflate(savedInstanceState)
    loadingView.inflate(savedInstanceState)

    owner.runOnDestroy {
      listView.teardown()
    }

    setupRefreshLatch()
    listenForControllerEvents()
  }

  override fun saveState(outState: Bundle) {
    listView.saveState(outState)
    loadingView.saveState(outState)
  }

  private fun setupRefreshLatch() {
    refreshLatch = RefreshLatch.create(owner, 150L) { loading ->
      if (loading) {
        listView.hide()
        loadingView.show()
      } else {
        loadingView.hide()
        listView.show()
      }
    }
  }

  private fun listenForControllerEvents() {
    controllerBus.listen()
        .subscribe {
          when (it) {
            is Loading -> refreshLatch.isRefreshing = true
            is LicensesLoaded -> listView.loadLicenses(it.libraries)
            is LoadError -> listView.showError(it.error)
            is LoadComplete -> refreshLatch.isRefreshing = false
          }
        }
        .destroy(owner)
  }

  override fun onUiEvent(): Observable<AboutViewEvents> {
    return uiBus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
  }

}