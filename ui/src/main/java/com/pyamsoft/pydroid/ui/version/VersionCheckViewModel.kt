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

package com.pyamsoft.pydroid.ui.version

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.onActualError
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import com.pyamsoft.pydroid.ui.version.VersionCheckControllerEvent.LaunchUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

internal class VersionCheckViewModel internal constructor(
    private val interactor: VersionInteractor
) : UiViewModel<VersionCheckViewState, VersionCheckViewEvent, VersionCheckControllerEvent>(
    initialState = VersionCheckViewState(
        isLoading = false,
        throwable = null,
    )
) {

    private val checkUpdateRunner = highlander<Unit, Boolean> { force ->
        handleVersionCheckBegin()
        try {
            val launcher = interactor.checkVersion(force)
            handleVersionCheckFound(launcher)
        } catch (error: Throwable) {
            error.onActualError { e ->
                Timber.e(e, "Error checking for latest version")
                handleVersionCheckError(e)
            }
        } finally {
            handleVersionCheckComplete()
        }
    }

    init {
        doOnBind {
            viewModelScope.launch(context = Dispatchers.Default) {
                interactor.watchForDownloadComplete {
                    Timber.d("App update download ready!")
                    publish(VersionCheckControllerEvent.ShowUpgrade)
                }
            }.also { doOnUnbind { it.cancel() } }
        }
    }

    override fun handleViewEvent(event: VersionCheckViewEvent) {
        return when (event) {
            is VersionCheckViewEvent.SnackbarHidden -> setState { copy(throwable = null) }
        }
    }

    private fun handleVersionCheckBegin() {
        setState { copy(isLoading = true) }
    }

    private fun handleVersionCheckFound(launcher: AppUpdateLauncher) {
        publish(LaunchUpdate(launcher))
    }

    private fun handleVersionCheckError(throwable: Throwable) {
        setState { copy(throwable = throwable) }
    }

    private fun handleVersionCheckComplete() {
        setState { copy(isLoading = false) }
    }

    internal fun checkForUpdates(force: Boolean) {
        Timber.d("Begin check for updates")
        viewModelScope.launch(context = Dispatchers.Default) { checkUpdateRunner.call(force) }
    }
}
