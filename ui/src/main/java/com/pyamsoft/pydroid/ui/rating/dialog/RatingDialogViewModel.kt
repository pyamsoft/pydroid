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

package com.pyamsoft.pydroid.ui.rating.dialog

import android.content.ActivityNotFoundException
import androidx.lifecycle.viewModelScope
import com.pyamsoft.highlander.highlander
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.bootstrap.rating.RatingInteractor
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogControllerEvent.CancelDialog
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogControllerEvent.NavigateRating
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.Cancel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.Rate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class RatingDialogViewModel internal constructor(
  private val interactor: RatingInteractor
) : UiViewModel<RatingDialogViewState, RatingDialogViewEvent, RatingDialogControllerEvent>(
    initialState = RatingDialogViewState(throwable = null)
) {

  private var saveRunner = highlander<Unit, String> { link ->
    withContext(Dispatchers.Default) { interactor.saveRating() }
    handleRate(link)
  }

  override fun onInit() {
  }

  override fun handleViewEvent(event: RatingDialogViewEvent) {
    return when (event) {
      is Rate -> save(event.link)
      is Cancel -> save("")
    }
  }

  private fun save(link: String) {
    viewModelScope.launch { saveRunner.call(link) }
  }

  private fun handleRate(link: String) {
    if (link.isBlank()) {
      publish(CancelDialog)
    } else {
      publish(NavigateRating(link))
    }
  }

  fun navigationFailed(error: ActivityNotFoundException) {
    setState { copy(throwable = error) }
  }

}
