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
import android.text.SpannedString
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

internal class RatingDialogViewModel internal constructor(
    interactor: RatingInteractor,
    debug: Boolean
) : UiViewModel<RatingDialogViewState, RatingDialogViewEvent, RatingDialogControllerEvent>(
    initialState = RatingDialogViewState(
        throwable = null,
        changelog = null,
        icon = 0
    ), debug = debug
) {

    private val saveRunner = highlander<Unit, String> { link ->
        interactor.saveRating()
        handleRate(link)
    }

    override fun handleViewEvent(event: RatingDialogViewEvent) {
        return when (event) {
            is Rate -> save(event.link)
            is Cancel -> save("")
        }
    }

    fun initialize(changelog: SpannedString, icon: Int) {
        this.setState { copy(changelog = changelog, icon = icon) }
    }

    private fun save(link: String) {
        viewModelScope.launch(context = Dispatchers.Default) { saveRunner.call(link) }
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

    fun navigationSuccess() {
        setState { copy(throwable = null) }
    }
}
