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

import android.view.ViewGroup
import com.pyamsoft.pydroid.arch.BaseUiView
import com.pyamsoft.pydroid.ui.databinding.AboutItemActionsBinding
import com.pyamsoft.pydroid.ui.internal.about.listitem.AboutItemViewEvent.OpenLibraryUrl
import com.pyamsoft.pydroid.ui.internal.about.listitem.AboutItemViewEvent.OpenLicenseUrl
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener

internal class AboutItemActionView internal constructor(
    parent: ViewGroup
) : BaseUiView<AboutItemViewState, AboutItemViewEvent, AboutItemActionsBinding>(parent) {

    override val viewBinding = AboutItemActionsBinding::inflate

    override val layoutRoot by boundView { aboutActions }

    init {
        doOnInflate {
            binding.actionViewLicense.setOnDebouncedClickListener { publish(OpenLicenseUrl) }
            binding.actionVisitHomepage.setOnDebouncedClickListener { publish(OpenLibraryUrl) }
        }
        doOnTeardown {
            binding.actionViewLicense.setOnDebouncedClickListener(null)
            binding.actionVisitHomepage.setOnDebouncedClickListener(null)
        }
    }

}
