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

import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.Loaded
import com.pyamsoft.pydroid.ui.databinding.RatingIconBinding

internal class RatingIconView internal constructor(
    private val imageLoader: ImageLoader,
    parent: ViewGroup
) : BaseUiView<RatingDialogViewState, RatingDialogViewEvent, RatingIconBinding>(parent) {

    override val viewBinding = RatingIconBinding::inflate

    override val layoutRoot by boundView { ratingIconRoot }

    private var iconLoaded: Loaded? = null

    init {
        doOnTeardown {
            clear()
        }
    }

    override fun onRender(state: RatingDialogViewState) {
        handleIcon(state)
    }

    private fun handleIcon(state: RatingDialogViewState) {
        state.icon.let { icon ->
            clear()
            if (icon != 0) {
                iconLoaded = imageLoader.load(icon)
                    .into(binding.icon)
            }
        }
    }

    private fun clear() {
        iconLoaded?.dispose()
        iconLoaded = null
    }
}
