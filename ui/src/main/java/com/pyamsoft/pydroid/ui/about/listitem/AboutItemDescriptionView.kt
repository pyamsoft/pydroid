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

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher
import com.pyamsoft.pydroid.ui.databinding.AboutItemDescriptionBinding

internal class AboutItemDescriptionView internal constructor(
  private val parent: ViewGroup
) : UiView<EMPTY>(EmptyPublisher), BaseAboutItem {

  private lateinit var binding: AboutItemDescriptionBinding

  override fun id(): Int {
    return binding.aboutDescription.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = AboutItemDescriptionBinding.inflate(parent.inflater(), parent, true)
  }

  override fun teardown() {
    binding.unbind()
  }

  override fun saveState(outState: Bundle) {
  }

  override fun bind(model: OssLibrary) {
    binding.aboutDescription.apply {
      text = model.description
      isVisible = model.description.isNotBlank()
    }
  }

  override fun unbind() {
    binding.aboutDescription.apply {
      text = ""
      isGone = true
    }
  }

}