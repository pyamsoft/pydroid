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
import android.widget.Button
import com.pyamsoft.pydroid.arch.BindingUiView
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemViewEvent.OpenLibraryUrl
import com.pyamsoft.pydroid.ui.about.listitem.AboutItemViewEvent.OpenLicenseUrl
import com.pyamsoft.pydroid.ui.databinding.AboutItemActionsBinding
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class AboutItemActionView internal constructor(
    parent: ViewGroup
) : BindingUiView<AboutItemViewState, AboutItemViewEvent, AboutItemActionsBinding>(parent) {

    override val layoutRoot by boundView { aboutActions }

    private val viewLicense by boundView<Button> { actionViewLicense }
    private val visitHomepage by boundView<Button> { actionVisitHomepage }

    init {
        doOnInflate {
            viewLicense.setOnDebouncedClickListener { publish(OpenLicenseUrl) }
            visitHomepage.setOnDebouncedClickListener { publish(OpenLibraryUrl) }
        }
        doOnTeardown {
            viewLicense.setOnDebouncedClickListener(null)
            visitHomepage.setOnDebouncedClickListener(null)
        }
    }

    override fun provideBindingInflater(): (LayoutInflater, ViewGroup) -> AboutItemActionsBinding {
        return AboutItemActionsBinding::inflate
    }

    override fun onRender(state: AboutItemViewState) {
    }
}
