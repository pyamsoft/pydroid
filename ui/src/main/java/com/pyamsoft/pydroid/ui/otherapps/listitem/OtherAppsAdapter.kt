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

package com.pyamsoft.pydroid.ui.otherapps.listitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

internal class OtherAppsAdapter internal constructor(
    private val owner: LifecycleOwner,
    private val callback: (event: OtherAppsItemViewEvent, index: Int) -> Unit
) : ListAdapter<OtherAppsItemViewState, BaseViewHolder<*>>(DIFFER) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).app.packageName.hashCode()
            .toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).app.packageName.isBlank()) {
            VIEW_TYPE_SPACER
        } else {
            VIEW_TYPE_REAL
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_REAL) {
            OtherAppsViewHolder.create(inflater, parent, owner, callback)
        } else {
            SpaceViewHolder.create(inflater, parent)
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<*>,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object {

        private const val VIEW_TYPE_SPACER = 1
        private const val VIEW_TYPE_REAL = 2

        private val DIFFER = object : DiffUtil.ItemCallback<OtherAppsItemViewState>() {
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
