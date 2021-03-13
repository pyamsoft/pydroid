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

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.onActualError
import com.pyamsoft.pydroid.bootstrap.version.AppUpdateLauncher
import com.pyamsoft.pydroid.bootstrap.version.VersionInteractor
import kotlinx.coroutines.CoroutineScope
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
    )
) {

    private val checkUpdateRunner = highlander<UpdateResult?, Boolean> { force ->
        try {
            val launcher = interactor.checkVersion(force)
            return@highlander UpdateResult(force, launcher)
        } catch (error: Throwable) {
            error.onActualError { e ->
                Timber.e(e, "Error checking for latest version")
                handleVersionCheckError(e)
            }
            return@highlander null
        }
    }

    private fun CoroutineScope.handleVersionCheckError(throwable: Throwable) {
        setState { copy(throwable = throwable) }
    }

    internal fun handleClearError() {
        setState { copy(throwable = null) }
    }

    internal fun handleVersionCheckComplete() {
        setState { copy(isLoading = false) }
    }

    internal fun handleClearNavigationError() {
        setState { copy(navigationError = null) }
    }

    internal inline fun checkForUpdates(
        scope: CoroutineScope,
        force: Boolean,
        crossinline onLaunch: (isFallbackEnabled: Boolean, launcher: AppUpdateLauncher) -> Unit
    ) {
        Timber.d("Begin check for updates")
        scope.launch(context = Dispatchers.Default) {
            setState(stateChange = { copy(isLoading = true) }, andThen = {
                checkUpdateRunner.call(force)?.let { result ->
                    onLaunch(result.isFallbackEnabled, result.launcher)
                }
            })
        }
    }

    internal fun navigationSuccess() {
        handleClearNavigationError()
    }

    internal fun navigationFailed(error: Throwable) {
        setState { copy(navigationError = error) }
    }

    internal inline fun watchForDownloadCompletion(
        scope: CoroutineScope,
        crossinline onComplete: () -> Unit
    ) {
        scope.launch(context = Dispatchers.Default) {
            interactor.watchForDownloadComplete {
                Timber.d("App update download ready!")
                onComplete()
            }
        }
    }

    internal data class UpdateResult internal constructor(
        val isFallbackEnabled: Boolean,
        val launcher: AppUpdateLauncher
    )
}
