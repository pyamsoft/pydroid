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

import androidx.lifecycle.Lifecycle
import com.pyamsoft.pydroid.core.bus.Listener
import com.pyamsoft.pydroid.ui.about.AboutEvents.LicensesLoaded
import com.pyamsoft.pydroid.ui.about.AboutEvents.LoadComplete
import com.pyamsoft.pydroid.ui.about.AboutEvents.LoadError
import com.pyamsoft.pydroid.ui.about.AboutEvents.Loading
import com.pyamsoft.pydroid.ui.arch.UiComponent
import com.pyamsoft.pydroid.ui.arch.destroy
import com.pyamsoft.pydroid.ui.widget.RefreshLatch
import io.reactivex.Observable

class AboutUiComponent internal constructor(
  private val listView: AboutListView,
  private val loadingView: AboutLoadingView,
  private val controllerBus: Listener<AboutEvents>
) : UiComponent<Unit> {

  private lateinit var refreshLatch: RefreshLatch

  override fun create(lifecycle: Lifecycle) {
    listView.inflate()
    loadingView.inflate()

    setupRefreshLatch(lifecycle)
    listenForControllerEvents(lifecycle)
  }

  private fun setupRefreshLatch(lifecycle: Lifecycle) {
    refreshLatch = RefreshLatch.create(lifecycle, 150L) { loading ->
      if (loading) {
        listView.hide()
        loadingView.show()
      } else {
        loadingView.hide()
        listView.show()
      }
    }
  }

  private fun listenForControllerEvents(lifecycle: Lifecycle) {
    controllerBus.listen()
        .subscribe {
          when (it) {
            is Loading -> refreshLatch.isRefreshing = true
            is LicensesLoaded -> listView.loadLicenses(it.libraries)
            is LoadError -> listView.showError(it.error)
            is LoadComplete -> refreshLatch.isRefreshing = false
          }
        }
        .destroy(lifecycle)
  }

  override fun onUiEvent(): Observable<Unit> {
    return Observable.empty()
  }

}