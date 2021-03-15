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

package com.pyamsoft.pydroid.ui.internal.otherapps

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class OtherAppsViewModel internal constructor(
    interactor: OtherAppsInteractor,
) : UiViewModel<OtherAppsViewState, OtherAppsControllerEvent>(
    initialState = OtherAppsViewState(
        apps = emptyList(),
        appsError = null,
        navigationError = null
    )
) {

    private val appsRunner = highlander<Unit, Boolean> { force ->
        interactor.getApps(force)
            .onSuccess { handleAppsLoaded(it) }
            .onFailure { handleHideError(it) }
    }

    init {
        // This may be cached in many cases
        viewModelScope.launch(context = Dispatchers.Default) {
            appsRunner.call(false)
        }
    }

    internal fun handleHideError() {
        viewModelScope.setState(stateChange = { copy(appsError = null) }, andThen = {
            publish(OtherAppsControllerEvent.LaunchFallback)
        })
    }

    internal fun handleOpenStoreUrl(index: Int) {
        openUrl(index) { it.storeUrl }
    }

    internal fun handleOpenSourceCodeUrl(index: Int) {
        openUrl(index) { it.sourceUrl }
    }

    private inline fun openUrl(
        index: Int,
        crossinline resolveUrl: (app: OtherApp) -> String,
    ) {
        val a = state.apps
        if (a.isNotEmpty()) {
            a.getOrNull(index)?.let { app ->
                publish(OtherAppsControllerEvent.OpenUrl(resolveUrl(app)))
            }
        }
    }

    private fun CoroutineScope.handleAppsLoaded(apps: List<OtherApp>) {
        setState { copy(apps = apps) }
    }

    private fun CoroutineScope.handleHideError(throwable: Throwable) {
        setState { copy(appsError = throwable) }
    }

    internal fun handleNavigationFailed(throwable: Throwable) {
        setState { copy(navigationError = throwable) }
    }

    fun handleNavigationSuccess() {
        handleHideNavigation()
    }

    internal fun handleHideNavigation() {
        setState { copy(navigationError = null) }
    }
}
