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

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UnitControllerEvent
import com.pyamsoft.pydroid.arch.onActualError
import com.pyamsoft.pydroid.bootstrap.about.AboutInteractor
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

internal class AboutViewModel internal constructor(
    interactor: AboutInteractor,
) : UiViewModel<AboutViewState, AboutViewEvent, UnitControllerEvent>(
    initialState = AboutViewState(
        isLoading = false,
        licenses = emptyList(),
        loadError = null,
        navigationError = null
    )
) {

    private val licenseRunner = highlander<Unit, Boolean> { force ->
        try {
            val licenses = interactor.loadLicenses(force)
            handleLicensesLoaded(licenses)
        } catch (error: Throwable) {
            error.onActualError { e ->
                Timber.e(e, "Error loading licenses")
                handleLicenseLoadError(e)
            }
        }
    }

    internal inline fun handleOpenLibrary(
        index: Int,
        crossinline onOpen: (String) -> Unit
    ) {
        return openUrl(index, resolveUrl = { it.libraryUrl }, onOpen)
    }

    internal inline fun handleOpenLicense(
        index: Int,
        crossinline onOpen: (String) -> Unit
    ) {
        return openUrl(index, resolveUrl = { it.licenseUrl }, onOpen)
    }

    private inline fun openUrl(
        index: Int,
        crossinline resolveUrl: (library: OssLibrary) -> String,
        crossinline onOpen: (String) -> Unit
    ) {
        val l = state.licenses
        if (l.isNotEmpty()) {
            l.getOrNull(index)?.let { lib ->
                onOpen(resolveUrl(lib))
            }
        }
    }

    internal fun handleLoadLicenses() {
        viewModelScope.setState(stateChange = { copy(isLoading = true) }, andThen = {
            licenseRunner.call(false)
            setState { copy(isLoading = false) }
        })
    }

    private fun CoroutineScope.handleLicensesLoaded(licenses: List<OssLibrary>) {
        setState { copy(licenses = licenses) }
    }

    private fun CoroutineScope.handleLicenseLoadError(throwable: Throwable) {
        setState { copy(loadError = throwable) }
    }

    internal fun navigationFailed(throwable: Throwable) {
        viewModelScope.setState { copy(navigationError = throwable) }
    }

    internal fun navigationSuccess() {
        handleHideNavigation()
    }

    internal fun handleClearLoadError() {
        viewModelScope.setState { copy(loadError = null) }
    }

    internal fun handleHideNavigation() {
        viewModelScope.setState { copy(navigationError = null) }
    }
}
