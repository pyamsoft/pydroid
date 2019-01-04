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

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutBinding
import com.pyamsoft.pydroid.ui.databinding.AdapterItemSpacerBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.util.show

internal class RealViewHolder internal constructor(
  private val binding: AdapterItemAboutBinding,
  private val activity: FragmentActivity
) : ViewHolder(binding.root) {

  override fun bind(model: OssLibrary) {
    binding.apply {
      aboutLibraryTitle.text = model.name
      aboutLibraryLicense.text = "License: ${model.licenseName}"
      aboutLibraryDescription.text = model.description
      aboutLibraryDescription.isVisible = model.description.isNotBlank()

      aboutLibraryVisitHomepage.setOnDebouncedClickListener {
        ViewLicenseDialog.newInstance(model.name, model.libraryUrl)
            .show(activity, ViewLicenseDialog.TAG)
      }

      aboutLibraryViewLicense.setOnDebouncedClickListener {
        ViewLicenseDialog.newInstance(model.name, model.licenseUrl)
            .show(activity, ViewLicenseDialog.TAG)
      }
    }
  }

  override fun unbind() {
    binding.apply {
      aboutLibraryTitle.text = null
      aboutLibraryLicense.text = null
      aboutLibraryDescription.text = null
      aboutLibraryDescription.isGone = true
      aboutLibraryVisitHomepage.setOnDebouncedClickListener(null)
      aboutLibraryViewLicense.setOnDebouncedClickListener(null)

      unbind()
    }
  }

}

internal class FakeViewHolder(binding: AdapterItemSpacerBinding) : ViewHolder(binding.root)

internal abstract class ViewHolder internal constructor(
  itemView: View
) : RecyclerView.ViewHolder(itemView) {

  open fun bind(model: OssLibrary) {

  }

  open fun unbind() {

  }

}
