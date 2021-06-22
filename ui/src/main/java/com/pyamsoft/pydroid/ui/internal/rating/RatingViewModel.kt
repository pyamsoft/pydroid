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
import com.pyamsoft.pydroid.core.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

internal class RatingViewModel
internal constructor(
    interactor: RatingInteractor,
) :
    UiViewModel<RatingViewState, RatingControllerEvent>(
        initialState = RatingViewState(navigationError = null)) {

  private val loadRunner =
      highlander<ResultWrapper<LoadResult>, Boolean> { force ->
        interactor.askForRating(force).map { LoadResult(force, it) }
      }

  internal fun load(force: Boolean) {
    viewModelScope.launch(context = Dispatchers.Default) {
      loadRunner
          .call(force)
          .onSuccess {
            publish(RatingControllerEvent.LaunchRating(it.isFallbackEnabled, it.launcher))
          }
          .onFailure { Timber.e(it, "Unable to launch rating flow") }
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

  private data class LoadResult(val isFallbackEnabled: Boolean, val launcher: AppRatingLauncher)
}
