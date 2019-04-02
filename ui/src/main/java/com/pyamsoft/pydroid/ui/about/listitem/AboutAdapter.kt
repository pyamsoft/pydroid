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
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.about.listitem.AboutAdapter.AdapterItem.Fake
import com.pyamsoft.pydroid.ui.about.listitem.AboutAdapter.AdapterItem.Real
import java.util.UUID

internal class AboutAdapter internal constructor(
  private val callback: AboutViewHolderUiComponent.Callback
) : RecyclerView.Adapter<BaseViewHolder>() {

  private val items: MutableList<AdapterItem> = ArrayList()

  init {
    setHasStableIds(true)
  }

  fun addAll(models: List<OssLibrary>) {
    val oldCount = itemCount

    if (items.isEmpty()) {
      items.add(Fake(UUID.randomUUID().toString()))
    }

    val realItems = models.map { Real(UUID.randomUUID().toString(), it) }
    items.addAll(realItems)

    notifyItemRangeInserted(oldCount, itemCount - 1)
  }

  fun clear() {
    val size = itemCount
    items.clear()
    notifyItemRangeRemoved(0, size - 1)
  }

  override fun getItemId(position: Int): Long {
    return items[position].id.hashCode()
        .toLong()
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
  ): BaseViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    if (viewType == VIEW_TYPE_REAL) {
      return AboutViewHolder.create(inflater, parent, callback)
    } else {
      return SpaceViewHolder.create(inflater, parent)
    }
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(
    holder: BaseViewHolder,
    position: Int
  ) {
    val item = items[position]
    if (item is Real) {
      holder.bind(item.library)
    }
  }

  override fun onViewRecycled(holder: BaseViewHolder) {
    super.onViewRecycled(holder)
    holder.unbind()
  }

  internal sealed class AdapterItem(open val id: String) {

    internal data class Real(
      override val id: String,
      val library: OssLibrary
    ) : AdapterItem(id)

    internal data class Fake(override val id: String) : AdapterItem(id)
  }

  companion object {

    private const val VIEW_TYPE_SPACER = 1
    private const val VIEW_TYPE_REAL = 2
  }
}
