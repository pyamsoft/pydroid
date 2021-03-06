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
      highlander<ResultWrapper<AppRatingLauncher>> { interactor.askForRating() }

  private val marketRunner =
      highlander<ResultWrapper<AppRatingLauncher>> { interactor.loadMarketLauncher() }

  internal fun loadMarketPage() {
    viewModelScope.launch(context = Dispatchers.Default) {
      marketRunner
          .call()
          .onSuccess { publish(RatingControllerEvent.LaunchMarketPage(it)) }
          .onFailure { e ->
            Timber.e(e, "Unable to launch market page")
            setState { copy(navigationError = e) }
          }
    }
  }

  internal fun loadInAppRating() {
    viewModelScope.launch(context = Dispatchers.Default) {
      loadRunner.call().onSuccess { publish(RatingControllerEvent.LaunchRating(it)) }.onFailure { e
        ->
        Timber.e(e, "Unable to launch rating flow")
        setState { copy(navigationError = e) }
      }
    }
  }

  internal fun handleClearNavigationError() {
    setState { copy(navigationError = null) }
  }
}
