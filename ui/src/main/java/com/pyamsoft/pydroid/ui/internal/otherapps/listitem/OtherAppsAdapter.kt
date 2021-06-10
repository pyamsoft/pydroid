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

package com.pyamsoft.pydroid.ui.internal.otherapps.listitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.ui.util.teardownAdapter
import me.zhanghai.android.fastscroll.PopupTextProvider

internal class OtherAppsAdapter
internal constructor(private val callback: (event: OtherAppsItemViewEvent, index: Int) -> Unit) :
    ListAdapter<OtherAppsItemViewState, OtherAppsViewHolder>(DIFFER), PopupTextProvider {

  init {
    setHasStableIds(true)
  }

  override fun getPopupText(position: Int): String {
    val item = getItem(position)
    return item.app.name.first().toUpperCase().toString()
  }

  override fun getItemId(position: Int): Long {
    return getItem(position).app.hashCode().toLong()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherAppsViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return OtherAppsViewHolder.create(inflater, parent, callback)
  }

  override fun onBindViewHolder(holder: OtherAppsViewHolder, position: Int) {
    val item = getItem(position)
    holder.bindState(item)
  }

  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    super.onDetachedFromRecyclerView(recyclerView)
    teardownAdapter(recyclerView)
  }

  override fun onViewRecycled(holder: OtherAppsViewHolder) {
    super.onViewRecycled(holder)
    holder.teardown()
  }

  companion object {

    private val DIFFER =
        object : DiffUtil.ItemCallback<OtherAppsItemViewState>() {
          override fun areItemsTheSame(
              oldItem: OtherAppsItemViewState,
              newItem: OtherAppsItemViewState
          ): Boolean {
            return oldItem.app.packageName == newItem.app.packageName
          }

          override fun areContentsTheSame(
              oldItem: OtherAppsItemViewState,
              newItem: OtherAppsItemViewState
          ): Boolean {
            return oldItem == newItem
          }
        }
  }
}
