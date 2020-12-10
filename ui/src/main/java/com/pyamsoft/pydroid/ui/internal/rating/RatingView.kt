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

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.arch.UiView
import com.pyamsoft.pydroid.bootstrap.rating.AppRatingLauncher
import com.pyamsoft.pydroid.ui.util.Snackbreak

internal class RatingView internal constructor(
    private val owner: LifecycleOwner,
    private val snackbarRootProvider: () -> ViewGroup
) : UiView<RatingViewState, RatingViewEvent>() {

    override fun render(state: UiRender<RatingViewState>) {
        state.distinctBy { it.rating }.render(viewScope) { handleRating(it) }
    }

    private fun handleRating(launcher: AppRatingLauncher?) {
        if (launcher == null) {
            clearRating()
        } else {
            showRating(launcher)
        }
    }

    private fun showRating(launcher: AppRatingLauncher) {
        Snackbreak.bindTo(owner) {
            long(snackbarRootProvider(), "Enjoying the app?", onHidden = { _, _ ->
                publish(RatingViewEvent.HideRating)
            }) {
                setAction("Rate") { publish(RatingViewEvent.LaunchRating(launcher)) }
            }
        }
    }

    private fun clearRating() {
        Snackbreak.bindTo(owner) {
            dismiss()
        }
    }
}