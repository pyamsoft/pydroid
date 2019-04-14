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

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.about.AboutUiComponent.Callback
import com.pyamsoft.pydroid.ui.about.AboutViewModel.AboutState
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import com.pyamsoft.pydroid.ui.navigation.NavigationViewModel
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView
import javax.inject.Inject

internal class AboutUiComponentImpl @Inject internal constructor(
  private val listView: AboutListView,
  private val spinner: SpinnerView,
  private val viewModel: AboutViewModel,
  private val navigationViewModel: NavigationViewModel
) : BaseUiComponent<Callback>(),
    AboutUiComponent {

  override fun id(): Int {
    throw InvalidIdException
  }

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: Callback
  ) {
    owner.doOnDestroy {
      listView.teardown()
      spinner.teardown()
      viewModel.unbind()
    }

    listView.inflate(savedInstanceState)
    spinner.inflate(savedInstanceState)
    viewModel.bind { state, oldState ->
      renderLoading(state, oldState)
      renderList(state, oldState)
      renderError(state, oldState)
      renderUrl(state, oldState)
    }
  }

  override fun onSaveState(outState: Bundle) {
    listView.saveState(outState)
    spinner.saveState(outState)
  }

  private fun renderLoading(
    state: AboutState,
    oldState: AboutState?
  ) {
    state.renderOnChange(oldState, value = { it.isLoading }) { loading ->
      if (loading) {
        listView.hide()
        spinner.show()
      } else {
        spinner.hide()
        listView.show()
      }
    }
  }

  private fun renderList(
    state: AboutState,
    oldState: AboutState?
  ) {
    state.renderOnChange(oldState, value = { it.licenses }) { licenses ->
      if (licenses.isEmpty()) {
        listView.clearLicenses()
      } else {
        listView.loadLicenses(licenses)
      }
    }
  }

  private fun renderError(
    state: AboutState,
    oldState: AboutState?
  ) {
    state.renderOnChange(oldState, value = { it.throwable }) { throwable ->
      if (throwable == null) {
        listView.clearError()
      } else {
        listView.showError(throwable)
      }
    }
  }

  private fun renderUrl(
    state: AboutState,
    oldState: AboutState?
  ) {
    state.renderOnChange(oldState, value = { it.url }) { url ->
      if (url.isNotBlank()) {
        callback.onNavigateExternalUrl(url)
      }
    }
  }

  override fun failedNavigation(error: ActivityNotFoundException) {
    navigationViewModel.failedNavigation(error)
  }

}