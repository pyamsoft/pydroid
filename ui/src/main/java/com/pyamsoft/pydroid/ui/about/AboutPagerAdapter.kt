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
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.about.AboutPagerAdapter.AdapterItem.Fake
import com.pyamsoft.pydroid.ui.about.AboutPagerAdapter.AdapterItem.Real
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutBinding

internal class AboutPagerAdapter internal constructor(
  private val activity: FragmentActivity
) : RecyclerView.Adapter<ViewHolder>() {

  private val items: MutableList<Any> = ArrayList()

  fun addAll(models: List<OssLibrary>) {
    val oldCount = itemCount

    if (items.isEmpty()) {
      items.add(Fake)
    }

    val realItems = models.map { Real(it) }
    items.addAll(realItems)

    notifyItemRangeInserted(oldCount, itemCount - 1)
  }

  fun clear() {
    val size = itemCount
    items.clear()
    notifyItemRangeRemoved(0, size - 1)
  }

  override fun getItemViewType(position: Int): Int {
    if (position == 0) {
      return VIEW_TYPE_SPACER
    } else {
      return VIEW_TYPE_REAL
    }
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    if (viewType == VIEW_TYPE_REAL) {
      val binding = AdapterItemAboutBinding.inflate(inflater, parent, false)
      return RealViewHolder(binding, activity)
    } else {
      return FakeViewHolder(parent)
    }
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    val item = items[position]
    if (item is Real) {
      holder.bind(item.library)
    }
  }

  override fun onViewRecycled(holder: ViewHolder) {
    super.onViewRecycled(holder)
    holder.unbind()
  }

  internal sealed class AdapterItem {
    data class Real(val library: OssLibrary) : AdapterItem()
    object Fake : AdapterItem()
  }

  companion object {

    private const val VIEW_TYPE_SPACER = 1
    private const val VIEW_TYPE_REAL = 2
  }
}
