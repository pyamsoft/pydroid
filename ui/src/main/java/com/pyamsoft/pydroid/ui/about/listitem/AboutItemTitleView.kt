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
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.databinding.AboutItemTitleBinding

internal class AboutItemTitleView internal constructor(
  private val parent: ViewGroup
) : UiView, BaseAboutItem {

  private lateinit var binding: AboutItemTitleBinding

  override fun id(): Int {
    return View.NO_ID
  }

  override fun inflate(savedInstanceState: Bundle?) {
    binding = AboutItemTitleBinding.inflate(parent.inflater(), parent, false)
    parent.addView(binding.root)
  }

  override fun saveState(outState: Bundle) {
  }

  @CheckResult
  private fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
    return binding.root.context.getString(id, *formatArgs)
  }

  override fun bind(model: OssLibrary) {
    binding.apply {
      aboutLibraryTitle.text = model.name
      aboutLibraryLicense.text = getString(R.string.license_name, model.licenseName)
    }
  }

  override fun unbind() {
    binding.apply {
      aboutLibraryTitle.text = ""
      aboutLibraryLicense.text = ""
    }
  }

}

