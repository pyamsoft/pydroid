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
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pydroid.arch.Bindable
import com.pyamsoft.pydroid.arch.bindViews
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import kotlinx.coroutines.CoroutineScope

internal class AboutViewHolder private constructor(
    view: View,
    owner: LifecycleOwner,
    callback: (event: AboutItemViewEvent, index: Int) -> Unit
) : BaseViewHolder(view) {

    private val binder: Bindable<AboutItemViewState>

    internal var titleView: AboutItemTitleView? = null
    internal var descriptionView: AboutItemDescriptionView? = null
    internal var actionView: AboutItemActionView? = null

    init {
        val parent = view.findViewById<ViewGroup>(R.id.about_listitem_root)
        Injector.obtain<PYDroidComponent>(itemView.context.applicationContext)
            .plusAboutItem()
            .create(parent)
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

    override fun bind(state: AboutItemViewState) {
        binder.bind(state)
    }

    override fun unbind() {
        binder.unbind()
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
            val view = inflater.inflate(R.layout.adapter_item_about_license, container, false)
            return AboutViewHolder(view, owner, callback)
        }
    }
}
