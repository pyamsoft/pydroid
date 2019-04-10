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

package com.pyamsoft.pydroid.ui.about.dialog

import com.pyamsoft.pydroid.arch.Presenter
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.about.dialog.UrlPresenter.UrlState
import com.pyamsoft.pydroid.ui.about.dialog.UrlWebviewState.ExternalNavigation
import com.pyamsoft.pydroid.ui.about.dialog.UrlWebviewState.Loading
import com.pyamsoft.pydroid.ui.about.dialog.UrlWebviewState.PageError
import com.pyamsoft.pydroid.ui.about.dialog.UrlWebviewState.PageLoaded

internal class UrlPresenter internal constructor(
  private val schedulerProvider: SchedulerProvider,
  private val bus: EventBus<UrlWebviewState>
) : Presenter<UrlState, UrlPresenter.Callback>() {

  override fun initialState(): UrlState {
    return UrlState(isLoading = false, reachedTargetPage = false, url = "")
  }

  override fun onBind() {
    bus.listen()
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .subscribe {
          return@subscribe when (it) {
            is Loading -> handleWebviewBegin()
            is PageLoaded -> handlePageLoaded(it.url)
            is PageError -> handlePageError(it.url)
            is ExternalNavigation -> callback.handleWebviewExternalNavigationEvent(it.url)
          }
        }
        .destroy()
  }

  override fun onUnbind() {
  }

  private fun handleWebviewBegin() {
    setState {
      copy(isLoading = true)
    }
  }

  private fun handlePageLoaded(url: String) {
    setState {
      copy(isLoading = false, reachedTargetPage = true, url = url)
    }
  }

  private fun handlePageError(url: String) {
    setState {
      copy(isLoading = false, reachedTargetPage = false, url = url)
    }
  }

  data class UrlState(
    val isLoading: Boolean,
    val reachedTargetPage: Boolean,
    val url: String
  )

  interface Callback : Presenter.Callback<UrlState> {

    fun handleWebviewExternalNavigationEvent(url: String)

  }

}
