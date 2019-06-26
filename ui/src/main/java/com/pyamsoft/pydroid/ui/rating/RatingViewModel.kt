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

package com.pyamsoft.pydroid.ui.rating

import androidx.lifecycle.viewModelScope
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.arch.UnitViewEvent
import com.pyamsoft.pydroid.arch.UnitViewState
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.arch.singleJob
import com.pyamsoft.pydroid.ui.rating.RatingControllerEvent.LoadRating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class RatingViewModel internal constructor(
  private val interactor: RatingInteractor
) : UiViewModel<UnitViewState, UnitViewEvent, RatingControllerEvent>(initialState = UnitViewState) {

  private var loadJob by singleJob()

  override fun onTeardown() {
    loadJob.cancel()
  }

  override fun handleViewEvent(event: UnitViewEvent) {
  }

  internal fun load(force: Boolean) {
    loadJob = viewModelScope.launch {
      val show = withContext(Dispatchers.Default) { interactor.needsToViewRating(force) }
      if (show) {
        publish(LoadRating)
      }
    }
  }

}
