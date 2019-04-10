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

import com.pyamsoft.pydroid.arch.Presenter
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.about.AboutPresenter.AboutState
import timber.log.Timber

internal class AboutPresenter internal constructor(
  private val interactor: AboutInteractor,
  private val schedulerProvider: SchedulerProvider
) : Presenter<AboutState, AboutPresenter.Callback>(),
    AboutListView.Callback {

  private var licenseDisposable by singleDisposable()

  override fun initialState(): AboutState {
    return AboutState(isLoading = false, throwable = null, licenses = emptyList())
  }

  override fun onBind() {
    loadLicenses(false)
  }

  override fun onUnbind() {
    licenseDisposable.tryDispose()
  }

  private fun loadLicenses(force: Boolean) {
    licenseDisposable = interactor.loadLicenses(force)
        .subscribeOn(schedulerProvider.backgroundScheduler)
        .observeOn(schedulerProvider.foregroundScheduler)
        .doOnSubscribe { handleLicenseLoadBegin() }
        .doAfterTerminate { handleLicenseLoadComplete() }
        .subscribe({ handleLicensesLoaded(it) }, {
          Timber.e(it, "Error loading licenses")
          handleLicenseLoadError(it)
        })
  }

  private fun handleLicenseLoadBegin() {
    setState {
      copy(isLoading = true)
    }
  }

  private fun handleLicensesLoaded(licenses: List<OssLibrary>) {
    setState {
      copy(licenses = licenses, throwable = null)
    }

  }

  private fun handleLicenseLoadError(throwable: Throwable) {
    setState {
      copy(licenses = emptyList(), throwable = throwable)
    }

  }

  private fun handleLicenseLoadComplete() {
    setState {
      copy(isLoading = false)
    }
  }

  override fun onViewLicenseClicked(
    name: String,
    licenseUrl: String
  ) {
    callback.handleViewLicense(name, licenseUrl)
  }

  override fun onVisitHomepageClicked(
    name: String,
    homepageUrl: String
  ) {
    callback.handleVisitHomepage(name, homepageUrl)
  }

  data class AboutState(
    val isLoading: Boolean,
    val throwable: Throwable?,
    val licenses: List<OssLibrary>
  )

  interface Callback : Presenter.Callback<AboutState> {

    fun handleViewLicense(
      name: String,
      licenseUrl: String
    )

    fun handleVisitHomepage(
      name: String,
      homepageUrl: String
    )
  }
}
