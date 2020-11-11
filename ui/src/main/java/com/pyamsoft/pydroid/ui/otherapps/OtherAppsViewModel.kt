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

package com.pyamsoft.pydroid.ui.otherapps

import android.content.ActivityNotFoundException
import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.otherapps.OtherAppsInteractor
import com.pyamsoft.pydroid.bootstrap.otherapps.api.OtherApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class OtherAppsViewModel internal constructor(
    interactor: OtherAppsInteractor,
) : UiViewModel<OtherAppsViewState, OtherAppsViewEvent, OtherAppsControllerEvent>(
    initialState = OtherAppsViewState(
        apps = emptyList(),
        navigationError = null
    )
) {

    private val appsRunner = highlander<Unit, Boolean> { force ->
        val apps = interactor.getApps(force)
        handleAppsLoaded(apps)
    }

    init {
        doOnBind {
            loadApps()
        }
    }

    override fun handleViewEvent(event: OtherAppsViewEvent) {
        return when (event) {
            is OtherAppsViewEvent.OpenStore -> openUrl(event.index) { it.storeUrl }
            is OtherAppsViewEvent.ViewSource -> openUrl(event.index) { it.sourceUrl }
        }
    }

    private inline fun openUrl(
        index: Int,
        crossinline func: (app: OtherApp) -> String
    ) {
        withState {
            val a = apps
            if (a.isNotEmpty()) {
                a.getOrNull(index)?.let { app ->
                    publish(OtherAppsControllerEvent.ExternalUrl(func(app)))
                }
            }
        }
    }

    private fun loadApps() {
        // This should be cached in many cases
        viewModelScope.launch(context = Dispatchers.Default) { appsRunner.call(false) }
    }

    private fun handleAppsLoaded(apps: List<OtherApp>) {
        setState { copy(apps = apps) }
    }

    fun navigationFailed(throwable: ActivityNotFoundException) {
        setState { copy(navigationError = throwable) }
    }

    fun navigationSuccess() {
        setState { copy(navigationError = null) }
    }
}
