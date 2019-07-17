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
import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.about.AboutListControllerEvent.ExternalUrl
import com.pyamsoft.pydroid.ui.about.AboutListControllerEvent.LicenseLoadError
import com.pyamsoft.pydroid.ui.about.AboutListControllerEvent.NavigationError
import com.pyamsoft.pydroid.ui.about.AboutListViewEvent.OpenUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class AboutListViewModel internal constructor(
  private val interactor: AboutInteractor
) : UiViewModel<AboutListState, AboutListViewEvent, AboutListControllerEvent>(
    initialState = AboutListState(
        isLoading = false,
        licenses = emptyList()
    )
) {

  private val licenseRunner = highlander<Unit, Boolean> { force ->
    handleLicenseLoadBegin()
    try {
      val licenses = withContext(Dispatchers.Default) { interactor.loadLicenses(force) }
      handleLicensesLoaded(licenses)
    } catch (error: Throwable) {
      error.onActualError { e ->
        Timber.e(e, "Error loading licenses")
        handleLicenseLoadError(e)
      }
    } finally {
      handleLicenseLoadComplete()
    }
  }

  override fun onInit() {
    loadLicenses(false)
  }

  override fun handleViewEvent(event: AboutListViewEvent) {
    return when (event) {
      is OpenUrl -> publish(ExternalUrl(event.url))
    }
  }

  private fun loadLicenses(force: Boolean) {
    viewModelScope.launch { licenseRunner.call(force) }
  }

  private fun handleLicenseLoadBegin() {
    setState { copy(isLoading = true) }
  }

  private fun handleLicensesLoaded(licenses: List<OssLibrary>) {
    setState { copy(licenses = listOf(OssLibrary.EMPTY) + licenses) }
  }

  private fun handleLicenseLoadError(throwable: Throwable) {
    publish(LicenseLoadError(throwable))
  }

  private fun handleLicenseLoadComplete() {
    setState { copy(isLoading = false) }
  }

  fun navigationFailed(throwable: ActivityNotFoundException) {
    publish(NavigationError(throwable))
  }
}
