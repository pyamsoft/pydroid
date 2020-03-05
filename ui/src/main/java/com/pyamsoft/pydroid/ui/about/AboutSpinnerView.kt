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

package com.pyamsoft.pydroid.ui.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.arch.BindingUiView
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.LoadingSpinnerBinding

internal class AboutSpinnerView internal constructor(
    parent: ViewGroup
) : BindingUiView<AboutViewState, AboutViewEvent, LoadingSpinnerBinding>(parent) {

    override val layout: Int = R.layout.loading_spinner

    override val layoutRoot by boundView { spinnerRoot }

    private val spinner by boundView { spinner }

    init {
        doOnTeardown {
            spinner.isVisible = false
        }
    }

    override fun provideBindingInflater(): (LayoutInflater, ViewGroup) -> LoadingSpinnerBinding {
        return LoadingSpinnerBinding::inflate
    }

    override fun onRender(state: AboutViewState) {
        state.isLoading.let { loading ->
            spinner.isVisible = loading
        }
    }
}
