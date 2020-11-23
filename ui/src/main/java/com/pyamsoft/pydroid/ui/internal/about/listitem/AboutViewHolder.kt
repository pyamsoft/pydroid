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
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.createViewBinder
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutLicenseBinding

internal class AboutViewHolder private constructor(
    binding: AdapterItemAboutLicenseBinding,
    callback: (event: AboutItemViewEvent, index: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root), ViewBinder<AboutItemViewState> {

    private val binder: ViewBinder<AboutItemViewState>

    internal var titleView: AboutItemTitleView? = null
    internal var descriptionView: AboutItemDescriptionView? = null
    internal var actionView: AboutItemActionView? = null

    init {
        Injector.obtain<PYDroidComponent>(itemView.context.applicationContext)
            .plusAboutItem()
            .create(binding.aboutListitemRoot)
            .inject(this)

        binder = createViewBinder(
            requireNotNull(titleView),
            requireNotNull(descriptionView),
            requireNotNull(actionView)
        ) {
            callback(it, adapterPosition)
        }
    }

    override fun bind(state: AboutItemViewState) {
        binder.bind(state)
    }

    override fun teardown() {
        binder.teardown()
        titleView = null
        descriptionView = null
        actionView = null
    }

    companion object {

        @CheckResult
        @JvmStatic
        fun create(
            inflater: LayoutInflater,
            container: ViewGroup,
            callback: (event: AboutItemViewEvent, index: Int) -> Unit
        ): AboutViewHolder {
            val binding = AdapterItemAboutLicenseBinding.inflate(inflater, container, false)
            return AboutViewHolder(binding, callback)
        }
    }
}
