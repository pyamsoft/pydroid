/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.ui.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.about.AboutPagerAdapter.ViewHolder
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutBinding
import com.pyamsoft.pydroid.ui.util.navigate
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.util.hyperlink

internal class AboutPagerAdapter(private val rootView: View) : RecyclerView.Adapter<ViewHolder>() {

  private val items: MutableList<OssLibrary> = ArrayList()

  fun addAll(models: List<OssLibrary>) {
    val oldCount = itemCount
    items.addAll(models)
    notifyItemRangeInserted(oldCount, itemCount - 1)
  }

  fun clear() {
    val size = itemCount
    items.clear()
    notifyItemRangeRemoved(0, size - 1)
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val binding = AdapterItemAboutBinding.inflate(inflater, parent, false)
    return ViewHolder(binding, rootView)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    holder.bind(items[position])
  }

  override fun onViewRecycled(holder: ViewHolder) {
    super.onViewRecycled(holder)
    holder.unbind()
  }

  internal class ViewHolder(
    private val binding: AdapterItemAboutBinding,
    private val rootView: View
  ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: OssLibrary) {
      binding.apply {
        aboutLibraryTitle.text = model.name
        aboutLibraryLicense.text = "License: ${model.licenseName}"
        aboutLibraryDescription.text = model.description
        aboutLibraryDescription.isVisible = model.description.isNotBlank()

        aboutLibraryVisitHomepage.setOnDebouncedClickListener {
          model.libraryUrl.hyperlink(it.context)
              .navigate(rootView)
        }
        aboutLibraryViewLicense.setOnDebouncedClickListener {
          model.licenseUrl.hyperlink(it.context)
              .navigate(rootView)
        }
      }
    }

    fun unbind() {
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
}
