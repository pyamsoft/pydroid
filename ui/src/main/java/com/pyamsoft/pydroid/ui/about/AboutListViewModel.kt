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
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.SchedulerProvider
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.core.singleJob
import com.pyamsoft.pydroid.ui.about.AboutListControllerEvent.ExternalUrl
import com.pyamsoft.pydroid.ui.about.AboutListViewEvent.OpenUrl
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class AboutListViewModel internal constructor(
  private val interactor: AboutInteractor,
  private val schedulerProvider: SchedulerProvider
) : UiViewModel<AboutListState, AboutListViewEvent, AboutListControllerEvent>(
    initialState = AboutListState(
        isLoading = false,
        throwable = null,
        licenses = emptyList()
    )
) {

  private var licenseJob by singleJob()

  init {
    loadLicenses(false)
  }

  override fun handleViewEvent(event: AboutListViewEvent) {
    return when (event) {
      is OpenUrl -> publish(ExternalUrl(event.url))
    }
  }

  private fun loadLicenses(force: Boolean) {
    licenseJob = viewModelScope.launch {
      val bgDispatcher = schedulerProvider.backgroundScheduler.asCoroutineDispatcher()

      handleLicenseLoadBegin()
      try {
        val licenses = withContext(bgDispatcher) { interactor.loadLicenses(force) }
        handleLicensesLoaded(licenses)
      } catch (e: Throwable) {
        if (e !is CancellationException) {
          Timber.e(e, "Error loading licenses")
          handleLicenseLoadError(e)
        }
      } finally {
        handleLicenseLoadComplete()
      }
    }
  }

  private fun handleLicenseLoadBegin() {
    setState { copy(isLoading = true) }
  }

  private fun handleLicensesLoaded(licenses: List<OssLibrary>) {
    setState { copy(licenses = listOf(OssLibrary.EMPTY) + licenses, throwable = null) }
  }

  private fun handleLicenseLoadError(throwable: Throwable) {
    setState { copy(licenses = emptyList(), throwable = throwable) }
  }

  private fun handleLicenseLoadComplete() {
    setState { copy(isLoading = false) }
  }

  fun navigationFailed(throwable: ActivityNotFoundException) {
    setState { copy(throwable = throwable) }
  }
}
