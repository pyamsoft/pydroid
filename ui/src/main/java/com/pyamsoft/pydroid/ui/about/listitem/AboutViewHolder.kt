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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.pyamsoft.pydroid.arch.createComponent
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibrary
import com.pyamsoft.pydroid.ui.Injector
import com.pyamsoft.pydroid.ui.PYDroidComponent
import com.pyamsoft.pydroid.ui.R
import com.pyamsoft.pydroid.ui.app.ListItemLifecycle
import com.pyamsoft.pydroid.ui.arch.factory

internal class AboutViewHolder private constructor(
    view: View,
    private val callback: (event: AboutItemControllerEvent) -> Unit
) : BaseViewHolder(view) {

    internal var titleView: AboutItemTitleView? = null
    internal var descriptionView: AboutItemDescriptionView? = null
    internal var actionView: AboutItemActionView? = null

    internal var factory: ViewModelProvider.Factory? = null
    private var viewModel: AboutItemViewModel? = null

    private val parent = view.findViewById<ViewGroup>(R.id.about_listitem_root)

    private var lifecycle: ListItemLifecycle? = null

    private fun injectViewModel() {
        viewModel = factory<AboutItemViewModel>(ViewModelStore()) { factory }.get()
    }

    override fun bind(model: OssLibrary) {
        Injector.obtain<PYDroidComponent>(itemView.context.applicationContext)
            .plusAboutItem()
            .create(parent, model)
            .inject(this)

        val owner = ListItemLifecycle()
        lifecycle?.unbind()
        lifecycle = owner
        injectViewModel()

        createComponent(
            null, owner,
            requireNotNull(viewModel),
            requireNotNull(titleView),
            requireNotNull(actionView),
            requireNotNull(descriptionView)
        ) { callback(it) }

        owner.bind()
    }

    override fun unbind() {
        lifecycle?.unbind()
        lifecycle = null

        viewModel = null
        titleView = null
        descriptionView = null
        actionView = null
        factory = null
    }

    companion object {

        @CheckResult
        @JvmStatic
        fun create(
            inflater: LayoutInflater,
            container: ViewGroup,
            callback: (event: AboutItemControllerEvent) -> Unit
        ): AboutViewHolder {
            val view = inflater.inflate(R.layout.adapter_item_about_license, container, false)
            return AboutViewHolder(view, callback)
        }
    }
}
