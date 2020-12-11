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

package com.pyamsoft.pydroid.ui.internal.version

import android.content.ActivityNotFoundException
import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.onActualError
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewEvent.ClearUpdate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewEvent.ErrorHidden
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewEvent.LaunchUpdate
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewEvent.LoadingHidden
import com.pyamsoft.pydroid.ui.internal.version.VersionCheckViewEvent.NavigationHidden
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

internal class VersionCheckViewModel internal constructor(
    private val interactor: VersionInteractor
) : UiViewModel<VersionCheckViewState, VersionCheckViewEvent, VersionCheckControllerEvent>(
    initialState = VersionCheckViewState(
        isLoading = false,
        throwable = null,
        navigationError = null,
        updater = null
    )
) {

    private val checkUpdateRunner = highlander<Unit, Boolean> { force ->
        handleVersionCheckBegin()
        try {
            val launcher = interactor.checkVersion(force)
            handleVersionCheckFound(force, launcher)
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
        viewModelScope.launch(context = Dispatchers.Default) {
            interactor.watchForDownloadComplete {
                Timber.d("App update download ready!")
                publish(VersionCheckControllerEvent.ShowUpgrade)
            }
        }
    }

    override fun handleViewEvent(event: VersionCheckViewEvent) {
        return when (event) {
            is ErrorHidden -> clearError()
            is LaunchUpdate -> launchUpdate(event.launcher)
            is LoadingHidden -> handleVersionCheckComplete()
            is ClearUpdate -> clearUpdate()
            is NavigationHidden -> clearNavigationError()
        }
    }

    private fun clearError() {
        setState { copy(throwable = null) }
    }

    private fun clearUpdate() {
        setState { copy(updater = null) }
    }

    private fun launchUpdate(launcher: AppUpdateLauncher) {
        setState { copy(updater = null) }

        // Do this regardless of current state
        publish(VersionCheckControllerEvent.LaunchUpdate(launcher))
    }

    private fun handleVersionCheckBegin() {
        setState { copy(isLoading = true) }
    }

    private fun handleVersionCheckFound(force: Boolean, launcher: AppUpdateLauncher?) {
        if (force && launcher != null) {
            launchUpdate(launcher)
        } else {
            setState { copy(updater = launcher) }
        }
    }

    private fun handleVersionCheckError(throwable: Throwable) {
        setState { copy(throwable = throwable) }
    }

    private fun handleVersionCheckComplete() {
        setState { copy(isLoading = false) }
    }

    private fun clearNavigationError() {
        setState { copy(navigationError = null) }
    }

    internal fun checkForUpdates(force: Boolean) {
        Timber.d("Begin check for updates")
        viewModelScope.launch(context = Dispatchers.Default) { checkUpdateRunner.call(force) }
    }

    internal fun navigationSuccess() {
        clearNavigationError()
    }

    internal fun navigationFailed(error: ActivityNotFoundException) {
        setState { copy(navigationError = error) }
    }
}
