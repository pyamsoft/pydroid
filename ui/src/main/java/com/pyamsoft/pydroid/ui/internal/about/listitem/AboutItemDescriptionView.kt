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

package com.pyamsoft.pydroid.ui.internal.about.listitem

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.arch.UiRender
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.databinding.AboutItemDescriptionBinding

internal class AboutItemDescriptionView internal constructor(parent: ViewGroup) :
    BaseUiView<AboutItemViewState, AboutItemViewEvent, AboutItemDescriptionBinding>(parent) {

  override val viewBinding = AboutItemDescriptionBinding::inflate

  override val layoutRoot by boundView { aboutDescription }

  init {
    doOnTeardown { clear() }
  }

  private fun clear() {
    binding.aboutDescription.apply {
      text = ""
      isGone = true
    }
  }

  override fun onRender(state: UiRender<AboutItemViewState>) {
    state.mapChanged { it.library }.render(viewScope) { handleLibrary(it) }
  }

  private fun handleLibrary(library: OssLibrary) {
    binding.aboutDescription.text = library.description
    binding.aboutDescription.isVisible = library.description.isNotBlank()
  }
}
