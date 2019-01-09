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

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.arch.UiToggleView
import com.pyamsoft.pydroid.ui.databinding.FragmentAboutLibrariesListBinding
import com.pyamsoft.pydroid.ui.util.Snackbreak

class AboutListView internal constructor(
  private val parent: ViewGroup
) : UiToggleView {

  private lateinit var binding: FragmentAboutLibrariesListBinding

  override fun inflate() {
    binding = FragmentAboutLibrariesListBinding.inflate(parent.inflater(), parent, false)
    parent.addView(binding.root)

    setupListView()
  }

  private fun setupListView() {
    TODO("not implemented")
  }

  override fun show() {
    binding.aboutList.isVisible = true
  }

  override fun hide() {
    binding.aboutList.isVisible = false
  }

  fun loadLicenses(libraries: List<OssLibrary>) {
    TODO("not implemented")
  }

  fun showError(error: Throwable) {
    Snackbreak.short(parent, error.localizedMessage)
        .show()
  }

}