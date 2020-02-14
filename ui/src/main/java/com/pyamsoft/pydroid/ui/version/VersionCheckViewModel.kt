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

package com.pyamsoft.pydroid.ui.version

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.version.VersionCheckInteractor
import com.pyamsoft.pydroid.ui.version.VersionControllerEvent.ShowUpgrade
import com.pyamsoft.pydroid.ui.version.VersionViewEvent.SnackbarHidden
import com.pyamsoft.pydroid.ui.version.VersionViewState.Loading
import com.pyamsoft.pydroid.ui.version.VersionViewState.UpgradePayload
import kotlinx.coroutines.launch
import timber.log.Timber

internal class VersionCheckViewModel internal constructor(
    interactor: VersionCheckInteractor,
    debug: Boolean
) : UiViewModel<VersionViewState, VersionViewEvent, VersionControllerEvent>(
    initialState = VersionViewState(
        isLoading = null,
        throwable = null
    ), debug = debug
) {

    private var notifyUpgrade: Boolean = true

    private val checkUpdateRunner = highlander<Unit, Boolean> { force ->
        handleVersionCheckBegin(force)
        try {
            val version = interactor.checkVersion(force)
            if (version != null && (force || notifyUpgrade)) {
                notifyUpgrade = false
                handleVersionCheckFound(version.currentVersion, version.newVersion)
            }
        } catch (error: Throwable) {
            error.onActualError { e ->
                Timber.e(e, "Error checking for latest version")
                handleVersionCheckError(e)
            }
        } finally {
            handleVersionCheckComplete()
        }
    }

    override fun handleViewEvent(event: VersionViewEvent) {
        return when (event) {
            is SnackbarHidden -> setState { copy(throwable = null) }
        }
    }

    private fun handleVersionCheckBegin(forced: Boolean) {
        setState { copy(isLoading = Loading(forced)) }
    }

    private fun handleVersionCheckFound(
        currentVersion: Int,
        newVersion: Int
    ) {
        publish(ShowUpgrade(UpgradePayload(currentVersion, newVersion)))
    }

    private fun handleVersionCheckError(throwable: Throwable) {
        setState { copy(throwable = throwable) }
    }

    private fun handleVersionCheckComplete() {
        setState { copy(isLoading = null) }
    }

    internal fun checkForUpdates(force: Boolean) {
        viewModelScope.launch { checkUpdateRunner.call(force) }
    }
}
