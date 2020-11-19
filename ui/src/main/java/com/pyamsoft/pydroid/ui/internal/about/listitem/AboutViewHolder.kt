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
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder
import com.pyamsoft.pydroid.arch.bindViews
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.databinding.AdapterItemAboutLicenseBinding
import com.pyamsoft.pydroid.util.doOnDestroy

internal class AboutViewHolder private constructor(
    binding: AdapterItemAboutLicenseBinding,
    owner: LifecycleOwner,
    callback: (event: AboutItemViewEvent, index: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val binder: ViewBinder<AboutItemViewState>

    internal var titleView: AboutItemTitleView? = null
    internal var descriptionView: AboutItemDescriptionView? = null
    internal var actionView: AboutItemActionView? = null

    init {
        Injector.obtain<PYDroidComponent>(itemView.context.applicationContext)
            .plusAboutItem()
            .create(binding.aboutListitemRoot)
            .inject(this)

        binder = bindViews(
            owner,
            requireNotNull(titleView),
            requireNotNull(descriptionView),
            requireNotNull(actionView)
        ) {
            callback(it, adapterPosition)
        }

        owner.doOnDestroy {
            titleView = null
            descriptionView = null
            actionView = null
        }
    }

    fun bind(state: AboutItemViewState) {
        binder.bind(state)
    }

    companion object {

        @CheckResult
        @JvmStatic
        fun create(
            inflater: LayoutInflater,
            container: ViewGroup,
            owner: LifecycleOwner,
            callback: (event: AboutItemViewEvent, index: Int) -> Unit
        ): AboutViewHolder {
            val binding = AdapterItemAboutLicenseBinding.inflate(inflater, container, false)
            return AboutViewHolder(binding, owner, callback)
        }
    }
}
