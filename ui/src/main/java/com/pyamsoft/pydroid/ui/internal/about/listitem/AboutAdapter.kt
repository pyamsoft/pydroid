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

package com.pyamsoft.pydroid.ui.internal.about.listitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import me.zhanghai.android.fastscroll.PopupTextProvider

internal class AboutAdapter internal constructor(
    private val owner: LifecycleOwner,
    private val callback: (event: AboutItemViewEvent, index: Int) -> Unit
) : ListAdapter<AboutItemViewState, AboutViewHolder>(DIFFER), PopupTextProvider {

    init {
        setHasStableIds(true)
    }

    override fun getPopupText(position: Int): String {
        val item = getItem(position)
        return item.library.name.first().toUpperCase().toString()
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).library.hashCode()
            .toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AboutViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AboutViewHolder.create(inflater, parent, owner, callback)
    }

    override fun onBindViewHolder(
        holder: AboutViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {

        private val DIFFER = object : DiffUtil.ItemCallback<AboutItemViewState>() {
            override fun areItemsTheSame(
                oldItem: AboutItemViewState,
                newItem: AboutItemViewState
            ): Boolean {
                return oldItem.library.libraryUrl == newItem.library.libraryUrl
            }

            override fun areContentsTheSame(
                oldItem: AboutItemViewState,
                newItem: AboutItemViewState
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
