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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.BindingUiView
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.RatingControlsBinding
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.Cancel
import com.pyamsoft.pydroid.ui.rating.dialog.RatingDialogViewEvent.Rate
import com.pyamsoft.pydroid.ui.util.Snackbreak
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class RatingControlsView internal constructor(
    rateLink: String,
    private val owner: LifecycleOwner,
    parent: ViewGroup
) : BindingUiView<RatingDialogViewState, RatingDialogViewEvent, RatingControlsBinding>(parent) {

    override val layout: Int = R.layout.rating_controls

    override val layoutRoot by boundView { ratingControlRoot }

    private val rateApplication by boundView { rateApplication }
    private val noThanks by boundView { noThanks }

    init {
        doOnInflate {
            rateApplication.setOnDebouncedClickListener { publish(Rate(rateLink)) }
            noThanks.setOnDebouncedClickListener { publish(Cancel) }
        }

        doOnTeardown {
            rateApplication.setOnClickListener(null)
            noThanks.setOnClickListener(null)
        }
    }

    override fun provideBindingInflater(): (LayoutInflater, ViewGroup) -> RatingControlsBinding {
        return RatingControlsBinding::inflate
    }

    override fun onRender(state: RatingDialogViewState) {
        state.throwable.let { throwable ->
            if (throwable == null) {
                clearError()
            } else {
                showError(throwable)
            }
        }
    }

    private fun showError(error: Throwable) {
        Snackbreak.bindTo(owner) {
            make(layoutRoot, error.message ?: "An unexpected error occurred.")
        }
    }

    private fun clearError() {
        Snackbreak.bindTo(owner) {
            dismiss()
        }
    }
}
