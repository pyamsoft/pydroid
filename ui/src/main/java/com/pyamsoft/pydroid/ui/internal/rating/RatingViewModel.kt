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

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.rating.AppRatingLauncher
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class RatingViewModel internal constructor(
    interactor: RatingInteractor,
) : UiViewModel<RatingViewState, RatingViewEvent, RatingControllerEvent>(
    initialState = RatingViewState(
        rating = null
    )
) {

    private val loadRunner = highlander<Unit, Boolean> { force ->
        val launcher = interactor.askForRating(force)
        handleRatingLaunch(force, launcher)
    }

    private fun handleRatingLaunch(force: Boolean, launcher: AppRatingLauncher?) {
        if (force && launcher != null) {
            launchRating(launcher)
        } else {
            setState { copy(rating = launcher) }
        }
    }

    private fun launchRating(launcher: AppRatingLauncher) {
        setState { copy(rating = null) }

        // Do this regardless of current state
        publish(RatingControllerEvent.LoadRating(launcher))
    }

    private fun clearRating() {
        setState { copy(rating = null) }
    }

    override fun handleViewEvent(event: RatingViewEvent) {
        return when (event) {
            is RatingViewEvent.LaunchRating -> launchRating(event.launcher)
            is RatingViewEvent.HideRating -> clearRating()
        }
    }

    internal fun load(force: Boolean) {
        viewModelScope.launch(context = Dispatchers.Default) { loadRunner.call(force) }
    }
}
