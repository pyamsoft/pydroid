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
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.ui.version.VersionControllerEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.version.VersionViewEvent.SnackbarHidden
import com.pyamsoft.pydroid.ui.version.VersionViewEvent.UpdateRestart
import com.pyamsoft.pydroid.ui.version.VersionViewState.Loading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

internal class VersionCheckViewModel internal constructor(
    private val interactor: VersionCheckInteractor,
    debug: Boolean
) : UiViewModel<VersionViewState, VersionViewEvent, VersionControllerEvent>(
    initialState = VersionViewState(
        isLoading = null,
        throwable = null,
        isUpdateAvailable = false
    ), debug = debug
) {

    private val checkUpdateRunner = highlander<Unit, Boolean> { force ->
        handleVersionCheckBegin(force)
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
                    setState { copy(isUpdateAvailable = true) }
                }
            }
        }

        doOnBind { savedInstanceState ->
            savedInstanceState.useIfAvailable<Boolean>(KEY_UPDATE_AVAILABLE) { isUpdateAvailable ->
                if (isUpdateAvailable) {
                    Timber.d("Restore update available status from savedInstanceState")
                    setState { copy(isUpdateAvailable = isUpdateAvailable) }
                }
            }
        }

        doOnSaveState { outState, state ->
            outState.put(KEY_UPDATE_AVAILABLE, state.isUpdateAvailable)
        }
    }

    override fun handleViewEvent(event: VersionViewEvent) {
        return when (event) {
            is SnackbarHidden -> setState { copy(throwable = null) }
            is UpdateRestart -> handleUpdateRestart()
        }
    }

    private fun handleUpdateRestart() {
        viewModelScope.launch(context = Dispatchers.Default) {
            Timber.d("Updating app, restart via update manager!")
            interactor.completeUpdate()
        }
    }

    private fun handleVersionCheckBegin(forced: Boolean) {
        setState { copy(isLoading = Loading(forced)) }
    }

    private fun handleVersionCheckFound(launcher: AppUpdateLauncher) {
        publish(ShowUpgrade(launcher))
    }

    private fun handleVersionCheckError(throwable: Throwable) {
        setState { copy(throwable = throwable) }
    }

    private fun handleVersionCheckComplete() {
        setState { copy(isLoading = null) }
    }

    internal fun checkForUpdates(force: Boolean) {
        Timber.d("Begin check for updates")
        viewModelScope.launch(context = Dispatchers.Default) { checkUpdateRunner.call(force) }
    }

    companion object {
        private const val KEY_UPDATE_AVAILABLE = "version_is_update_available"
    }
}
