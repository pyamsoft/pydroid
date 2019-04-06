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
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.about.AboutPresenter.AboutState
import com.pyamsoft.pydroid.ui.about.AboutUiComponent.Callback
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import com.pyamsoft.pydroid.ui.widget.spinner.SpinnerView

internal class AboutUiComponentImpl internal constructor(
  private val listView: AboutListView,
  private val spinner: SpinnerView,
  private val presenter: AboutPresenter
) : BaseUiComponent<AboutUiComponent.Callback>(),
    AboutUiComponent,
    AboutPresenter.Callback {

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
      presenter.unbind()
    }

    listView.inflate(savedInstanceState)
    spinner.inflate(savedInstanceState)
    presenter.bind(this)
  }

  override fun onSaveState(outState: Bundle) {
    listView.saveState(outState)
    spinner.saveState(outState)
  }

  override fun onRender(
    state: AboutState,
    oldState: AboutState?
  ) {
    renderLoading(state, oldState)
    renderList(state, oldState)
    renderError(state, oldState)
  }

  private fun renderLoading(
    state: AboutState,
    oldState: AboutState?
  ) {
    state.isLoading.let { loading ->
      if (oldState == null || loading != oldState.isLoading) {
        if (loading) {
          listView.hide()
          spinner.show()
        } else {
          spinner.hide()
          listView.show()
        }
      }
    }
  }

  private fun renderList(
    state: AboutState,
    oldState: AboutState?
  ) {
    state.licenses.let { licenses ->
      if (oldState == null || licenses != oldState.licenses) {
        if (licenses.isEmpty()) {
          listView.clearLicenses()
        } else {
          listView.loadLicenses(licenses)
        }
      }
    }
  }

  private fun renderError(
    state: AboutState,
    oldState: AboutState?
  ) {
    state.throwable.let { throwable ->
      if (oldState == null || throwable != oldState.throwable) {
        if (throwable == null) {
          listView.clearError()
        } else {
          listView.showError(throwable)
        }
      }
    }
  }

  override fun handleViewLicense(
    name: String,
    licenseUrl: String
  ) {
    callback.showLicense(name, licenseUrl)
  }

  override fun handleVisitHomepage(
    name: String,
    homepageUrl: String
  ) {
    callback.navigateToHomepage(name, homepageUrl)
  }

}