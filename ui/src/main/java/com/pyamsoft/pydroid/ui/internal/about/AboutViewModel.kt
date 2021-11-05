/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pydroid.ui.internal.about

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import kotlinx.coroutines.CoroutineScope

internal class AboutViewModel
internal constructor(
    interactor: AboutInteractor,
) :
    UiViewModel<AboutViewState, AboutControllerEvent>(
        initialState =
            AboutViewState(
                isLoading = false,
                licenses = emptyList(),
                navigationError = null,
            )) {

  private val licenseRunner = highlander<List<OssLibrary>, Boolean> { interactor.loadLicenses(it) }

  internal fun handleOpenLibrary(index: Int) {
    return openUrl(index) { it.libraryUrl }
  }

  internal fun handleOpenLicense(index: Int) {
    return openUrl(index) { it.licenseUrl }
  }

  private inline fun openUrl(
      index: Int,
      crossinline resolveUrl: (library: OssLibrary) -> String,
  ) {
    val l = state.licenses
    if (l.isNotEmpty()) {
      l.getOrNull(index)?.let { lib -> publish(AboutControllerEvent.OpenUrl(resolveUrl(lib))) }
    }
  }

  internal fun handleLoadLicenses() {
    setState(
        stateChange = { copy(isLoading = true) },
        andThen = {
          val licenses = licenseRunner.call(false)
          handleLicensesLoaded(licenses)
          setState { copy(isLoading = false) }
        })
  }

  private fun CoroutineScope.handleLicensesLoaded(licenses: List<OssLibrary>) {
    setState { copy(licenses = licenses) }
  }

  internal fun navigationFailed(throwable: Throwable) {
    setState { copy(navigationError = throwable) }
  }

  internal fun navigationSuccess() {
    handleHideNavigationError()
  }

  internal fun handleHideNavigationError() {
    setState { copy(navigationError = null) }
  }
}
