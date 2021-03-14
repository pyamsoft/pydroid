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

package com.pyamsoft.pydroid.ui.internal.rating

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UnitControllerEvent
import com.pyamsoft.pydroid.arch.onActualError
import com.pyamsoft.pydroid.bootstrap.rating.AppRatingLauncher
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

internal class RatingViewModel internal constructor(
    interactor: RatingInteractor,
) : UiViewModel<RatingViewState, RatingViewEvent, UnitControllerEvent>(
    initialState = RatingViewState(navigationError = null)
) {

    private val loadRunner = highlander<LoadResult?, Boolean> { force ->
        try {
            val launcher = interactor.askForRating(force)
            return@highlander LoadResult(force, launcher)
        } catch (throwable: Throwable) {
            throwable.onActualError {
                Timber.e(throwable, "Unable to launch rating flow")
            }
            return@highlander null
        }
    }

    internal inline fun load(
        scope: CoroutineScope,
        force: Boolean,
        crossinline onLaunch: (isFallbackEnabled: Boolean, launcher: AppRatingLauncher) -> Unit
    ) {
        scope.launch(context = Dispatchers.Default) {
            loadRunner.call(force)?.let { result ->
                onLaunch(result.isFallbackEnabled, result.launcher)
            }
        }
    }

    internal fun handleClearNavigationError() {
        setState { copy(navigationError = null) }
    }

    internal fun handleNavigationSuccess() {
        handleClearNavigationError()
    }

    internal fun handleNavigationFailed(error: Throwable) {
        setState { copy(navigationError = error) }
    }

    internal data class LoadResult internal constructor(
        val isFallbackEnabled: Boolean,
        val launcher: AppRatingLauncher
    )
}
