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

package com.pyamsoft.pydroid.ui.rating

import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.bootstrap.rating.AppReviewLauncher
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class RatingViewModel internal constructor(
    interactor: RatingInteractor,
) : UiViewModel<UnitViewState, UnitViewEvent, RatingControllerEvent>(initialState = UnitViewState) {

    private val loadRunner = highlander<Unit, Boolean> { force ->
        val launcher = interactor.askForRating(force)
        handleRatingLaunch(launcher)
    }

    init {
        load(false)
    }

    private fun handleRatingLaunch(launcher: AppReviewLauncher) {
        publish(RatingControllerEvent.LoadRating(launcher))
    }

    override fun handleViewEvent(event: UnitViewEvent) {
    }

    internal fun load(force: Boolean) {
        viewModelScope.launch(context = Dispatchers.Default) { loadRunner.call(force) }
    }
}
