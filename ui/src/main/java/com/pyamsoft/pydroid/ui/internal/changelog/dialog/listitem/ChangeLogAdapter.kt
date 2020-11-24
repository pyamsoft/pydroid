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

package com.pyamsoft.pydroid.ui.internal.changelog.dialog.listitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.ui.util.teardownAdapter
import me.zhanghai.android.fastscroll.PopupTextProvider

internal class ChangeLogAdapter internal constructor(
) : ListAdapter<ChangeLogItemViewState, ChangeLogViewHolder>(DIFFER), PopupTextProvider {

    init {
        setHasStableIds(true)
    }

    override fun getPopupText(position: Int): String {
        val item = getItem(position)
        return item.line.line.first().toUpperCase().toString()
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).line.hashCode()
            .toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChangeLogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChangeLogViewHolder.create(inflater, parent)
    }

    override fun onBindViewHolder(
        holder: ChangeLogViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        teardownAdapter(recyclerView)
    }

    companion object {

        private val DIFFER = object : DiffUtil.ItemCallback<ChangeLogItemViewState>() {
            override fun areItemsTheSame(
                oldItem: ChangeLogItemViewState,
                newItem: ChangeLogItemViewState
            ): Boolean {
                return oldItem.line.line == newItem.line.line
            }

            override fun areContentsTheSame(
                oldItem: ChangeLogItemViewState,
                newItem: ChangeLogItemViewState
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
