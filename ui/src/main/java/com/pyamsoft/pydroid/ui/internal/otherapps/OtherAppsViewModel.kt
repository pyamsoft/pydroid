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
) : UiViewModel<OtherAppsViewState, OtherAppsViewEvent, OtherAppsControllerEvent>(
    initialState = OtherAppsViewState(
        apps = emptyList(),
        appsError = null,
        navigationError = null
    )
) {

    private val appsRunner = highlander<Unit, Boolean> { force ->
        interactor.getApps(force)
            .onSuccess { handleAppsLoaded(it) }
            .onFailure { handleAppsError(it) }
    }

    init {
        loadApps()
    }

    override fun handleViewEvent(event: OtherAppsViewEvent) = when (event) {
        is OtherAppsViewEvent.ListEvent.OpenStore -> openUrl(event.index) { it.storeUrl }
        is OtherAppsViewEvent.ListEvent.ViewSource -> openUrl(event.index) { it.sourceUrl }
        is OtherAppsViewEvent.ErrorEvent.HideNavigationError -> clearNavigationError()
        is OtherAppsViewEvent.ErrorEvent.HideAppsError -> hideAppsErrorAndLaunchFallback()
    }

    private fun hideAppsErrorAndLaunchFallback() {
        viewModelScope.launch(context = Dispatchers.Default) {
            setState(stateChange = { copy(appsError = null) }, andThen = {
                publish(OtherAppsControllerEvent.FallbackEvent)
            })
        }
    }

    private inline fun openUrl(
        index: Int,
        crossinline func: (app: OtherApp) -> String
    ) {
        val a = state.apps
        if (a.isNotEmpty()) {
            a.getOrNull(index)?.let { app ->
                publish(OtherAppsControllerEvent.ExternalUrl(func(app)))
            }
        }
    }

    private fun loadApps() {
        // This should be cached in many cases
        viewModelScope.launch(context = Dispatchers.Default) { appsRunner.call(false) }
    }

    private fun CoroutineScope.handleAppsLoaded(apps: List<OtherApp>) {
        setState { copy(apps = apps) }
    }

    private fun CoroutineScope.handleAppsError(throwable: Throwable) {
        setState { copy(appsError = throwable) }
    }

    internal fun navigationFailed(throwable: Throwable) {
        setState { copy(navigationError = throwable) }
    }

    fun navigationSuccess() {
        clearNavigationError()
    }

    private fun clearNavigationError() {
        setState { copy(navigationError = null) }
    }
}
