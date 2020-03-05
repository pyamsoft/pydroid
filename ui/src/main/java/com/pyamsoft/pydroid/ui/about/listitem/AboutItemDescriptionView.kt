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

package com.pyamsoft.pydroid.ui.about.listitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.arch.BindingUiView
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.databinding.AboutItemDescriptionBinding

internal class AboutItemDescriptionView internal constructor(
    parent: ViewGroup
) : BindingUiView<AboutItemViewState, AboutItemViewEvent, AboutItemDescriptionBinding>(parent) {

    override val layout: Int = R.layout.about_item_description

    override val layoutRoot by boundView { aboutDescription }

    init {
        doOnTeardown {
            clear()
        }
    }

    override fun provideBindingInflater(): (LayoutInflater, ViewGroup) -> AboutItemDescriptionBinding {
        return AboutItemDescriptionBinding::inflate
    }

    private fun clear() {
        layoutRoot.apply {
            text = ""
            isGone = true
        }
    }

    override fun onRender(state: AboutItemViewState) {
        state.library.let { library ->
            layoutRoot.text = library.description
            layoutRoot.isVisible = library.description.isNotBlank()
        }
    }
}
