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

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.base.about.AboutLibrariesModel
import com.pyamsoft.pydroid.ui.about.AboutPagerAdapter.ViewHolder
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutBinding
import com.pyamsoft.pydroid.ui.util.navigate
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.util.hyperlink

internal class AboutPagerAdapter(private val activity: FragmentActivity) : RecyclerView.Adapter<ViewHolder>() {

  private val items: MutableList<AboutLibrariesModel> = ArrayList()

  fun addAll(models: List<AboutLibrariesModel>) {
    val oldCount = itemCount
    items.addAll(models)
    notifyItemRangeInserted(oldCount, itemCount - 1)
  }

  fun clear() {
    val size = itemCount
    items.clear()
    notifyItemRangeRemoved(0, size - 1)
  }

  @CheckResult
  fun getTitleAt(position: Int): String {
    return items[position].name
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return ViewHolder(AdapterItemAboutBinding.inflate(inflater, parent, false))
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    holder.bind(activity, items[position])
  }

  override fun onViewRecycled(holder: ViewHolder) {
    super.onViewRecycled(holder)
    holder.unbind()
  }

  internal class ViewHolder(
    private val binding: AdapterItemAboutBinding
  ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
      activity: FragmentActivity,
      model: AboutLibrariesModel
    ) {
      val license = model.license
      val homepage = model.homepage
      binding.apply {
        aboutItemWebview.settings.defaultFontSize = 12
        aboutItemWebview.isVerticalScrollBarEnabled = true
        aboutItemWebview.loadDataWithBaseURL(null, license, "text/plain", "UTF-8", null)

        aboutItemHomepage.paintFlags = (aboutItemHomepage.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
        aboutItemHomepage.text = homepage
        aboutItemHomepage.setOnDebouncedClickListener {
          homepage.hyperlink(it.context)
              .navigate(activity, itemView)
        }
      }
    }

    fun unbind() {
      binding.apply {
        aboutItemHomepage.text = null
        aboutItemHomepage.setOnDebouncedClickListener(null)
        aboutItemWebview.loadDataWithBaseURL(null, null, "text/plain", "UTF-8", null)
      }
    }

  }
}