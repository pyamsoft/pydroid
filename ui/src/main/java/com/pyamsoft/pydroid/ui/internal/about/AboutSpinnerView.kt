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

package com.pyamsoft.pydroid.ui.internal.about

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.ui.databinding.AboutLibrariesLoadingBinding

internal class AboutSpinnerView internal constructor(
    parent: ViewGroup
) : BaseUiView<AboutViewState, AboutViewEvent, AboutLibrariesLoadingBinding>(parent) {

    override val viewBinding = AboutLibrariesLoadingBinding::inflate

    override val layoutRoot by boundView { spinnerRoot }

    init {
        doOnTeardown {
            binding.spinner.isVisible = false
        }
    }

    private fun handleLoading(loading: Boolean) {
        binding.spinner.isVisible = loading
    }

    override fun onRender(state: UiRender<AboutViewState>) {
        state.distinctBy { it.isLoading }.render { handleLoading(it) }
    }
}
